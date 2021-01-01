package com.duke.elliot.biblereadinghabits.bible_reading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.base.BaseFragment
import com.duke.elliot.biblereadinghabits.bible_reading.bookmark.BibleBookmarkUtil
import com.duke.elliot.biblereadinghabits.database.BibleVerse
import com.duke.elliot.biblereadinghabits.databinding.FragmentBibleReadingBinding
import com.duke.elliot.biblereadinghabits.main.MainApplication
import com.flyco.dialog.widget.NormalListDialog
import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer2
import kotlinx.coroutines.*

class BibleReadingFragment: BaseFragment() {

    private lateinit var binding: FragmentBibleReadingBinding
    private lateinit var viewModel: BibleReadingViewModel
    private lateinit var onPageChangeCallback: ViewPager2.OnPageChangeCallback

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_bible_reading,
            container,
            false
        )

        val bibleReadingFragmentArgs by navArgs<BibleReadingFragmentArgs>()
        val bookPage = bibleReadingFragmentArgs.bookPage

        val viewModelFactory = BibleReadingViewModelFactory(requireActivity().application, bookPage)
        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[BibleReadingViewModel::class.java]

        setOnOptionsItemSelectedListeners(
            R.id.add_to_bookmark to {
                BibleBookmarkUtil.addToBookmarks(
                    requireContext(),
                    viewModel.currentBookPage,
                    viewModel.firstBibleVerseOnCurrentPage
                )
                showToast(getString(R.string.add_to_bookmark_success))
            },
            R.id.select_book to {
                val normalListDialog =
                    NormalListDialog(requireContext(), viewModel.books)
                normalListDialog.title(getString(R.string.select_book))
                normalListDialog.titleBgColor(MainApplication.primaryThemeColor)
                normalListDialog.setOnOperItemClickL { _, _, position, _ ->
                    coroutineScope.launch {
                        viewModel.currentBookPage.book = position.inc()
                        viewModel.currentBookPage.page = 0
                        viewModel.currentBook.value = position.inc()
                        normalListDialog.dismiss()
                    }
                }
                normalListDialog.show()
            },
            R.id.show_bookmarks to {
                val bookmarks = BibleBookmarkUtil.getBookmarks(requireContext())

                if (bookmarks.isNotEmpty()) {
                    val bookmarkTexts = bookmarks.map { it.second }
                    val normalListDialog =
                        NormalListDialog(requireContext(), bookmarkTexts.toTypedArray())
                    normalListDialog.title(getString(R.string.bookmark))
                    normalListDialog.titleBgColor(MainApplication.primaryThemeColor)
                    normalListDialog.setOnOperItemClickL { _, _, position, _ ->
                        val bookmark = bookmarks[position].first
                        viewModel.currentBookPage.book = bookmark.book
                        viewModel.currentBookPage.page = bookmark.page
                        viewModel.currentBook.value = bookmark.book

                        normalListDialog.dismiss()
                    }
                    normalListDialog.show()
                } else
                    showToast(getString(R.string.bookmarks_empty_message))
            },
            R.id.delete_bookmark to {
                val bookmarks = BibleBookmarkUtil.getBookmarks(requireContext())

                if (bookmarks.isNotEmpty()) {
                    val bookmarkTexts = bookmarks.map { it.second }
                    val normalListDialog =
                        NormalListDialog(requireContext(), bookmarkTexts.toTypedArray())
                    normalListDialog.title(getString(R.string.delete_bookmark))
                    normalListDialog.titleBgColor(MainApplication.primaryThemeColor)
                    normalListDialog.setOnOperItemClickL { _, _, position, _ ->
                        BibleBookmarkUtil.deleteFromBookmarks(requireContext(), position)
                        normalListDialog.dismiss()
                    }
                    normalListDialog.show()
                } else
                    showToast(getString(R.string.bookmarks_empty_message))
            }
        )

        setToolbarFont(
            binding.toolbar,
            R.style.NanumMyeonjoExtraBoldTextAppearance
        )
        setBackgroundColor(binding.toolbar)
        initViewPager2()

        viewModel.currentBook.observe(
            viewLifecycleOwner, Observer { book ->
                coroutineScope.launch {
                    val bibleVerses = withContext(Dispatchers.IO) {
                            viewModel.bibleVerseDao.getBookValue(book)
                        }

                    val displayBibleVerses = mutableListOf<List<BibleVerse>>()
                    val groupedBibleVerses = bibleVerses.groupBy { it.chapter }

                    val keys = groupedBibleVerses.keys.sorted()
                    // Dividing into 5.
                    for (key in keys) {
                        val values = groupedBibleVerses[key] ?: continue
                        val sortedValues = values.sortedBy { it.verse }
                        displayBibleVerses.addAll(sortedValues.chunked(5))
                    }

                    val currentPage = viewModel.currentBookPage.page

                    val bibleVersesAdapter =
                        BibleVersesAdapter(requireActivity(), displayBibleVerses)
                    binding.bibleVerseViewPager2.adapter = bibleVersesAdapter
                    binding.bibleVerseViewPager2.post {
                        binding.bibleVerseViewPager2.setCurrentItem(currentPage, false)
                    }

                    val bookFlipPageTransformer = BookFlipPageTransformer2()
                    bookFlipPageTransformer.isEnableScale = true
                    bookFlipPageTransformer.scaleAmountPercent = 5F
                    binding.bibleVerseViewPager2.offscreenPageLimit = OFFSCREEN_PAGE_LIMIT_DEFAULT
                    binding.bibleVerseViewPager2.setPageTransformer(bookFlipPageTransformer)
                }
            })

        setDisplayHomeAsUpEnabled(binding.toolbar)
        setOptionsMenu(binding.toolbar, R.menu.menu_bible_reading)
        setOnHomePressedCallback {
            findNavController().popBackStack()
        }

        return binding.root
    }

    private fun initViewPager2() {
        onPageChangeCallback = object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val item = (binding.bibleVerseViewPager2.adapter as BibleVersesAdapter).getItem(position)
                val bibleVerse = item[0]
                viewModel.currentBookPage.book = bibleVerse.book
                viewModel.currentBookPage.page = position
                viewModel.firstBibleVerseOnCurrentPage = bibleVerse
                super.onPageSelected(position)
            }
        }

        binding.bibleVerseViewPager2.registerOnPageChangeCallback(onPageChangeCallback)
    }

    override fun onStop() {
        super.onStop()
        binding.bibleVerseViewPager2.unregisterOnPageChangeCallback(onPageChangeCallback)
        viewModel.saveLastBookPageRead()
    }

    private inner class BibleVersesAdapter(
        fragmentActivity: FragmentActivity,
        private val bibleVerses: List<List<BibleVerse>>
    ) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = bibleVerses.count()

        override fun createFragment(position: Int): Fragment {
            val fragment = BiblePageFragment()
            val bundle = Bundle()
            bundle.putParcelableArrayList(KEY_BIBLE_VERSES, bibleVerses[position] as ArrayList)
            fragment.arguments = bundle

            return fragment
        }

        fun getItem(position: Int) = bibleVerses[position]
    }

    companion object {
        const val KEY_BIBLE_VERSES = "com.duke.elliot.biblereadinghabits.bible_reading" +
                ".bible_reading_fragment.key_bible_verses"
    }
}