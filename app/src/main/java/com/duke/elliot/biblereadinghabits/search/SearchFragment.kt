package com.duke.elliot.biblereadinghabits.search

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.base.BaseFragment
import com.duke.elliot.biblereadinghabits.database.BibleVerse
import com.duke.elliot.biblereadinghabits.databinding.FragmentSearchBinding
import com.duke.elliot.biblereadinghabits.main.MainApplication
import com.duke.elliot.biblereadinghabits.util.BibleVerseUtil
import com.duke.elliot.biblereadinghabits.util.fadeIn
import com.duke.elliot.biblereadinghabits.util.fadeOut
import kotlinx.coroutines.*

class SearchFragment: BaseFragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SearchViewModel

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        val viewModelFactory = SearchViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[SearchViewModel::class.java]

        setDisplayHomeAsUpEnabled(binding.toolbar)
        setOnHomePressedCallback {
            findNavController().popBackStack()
        }

        setToolbarFont(binding.toolbar, R.style.NanumMyeonjoExtraBoldTextAppearance)
        setBackgroundColor(binding.toolbar)

        initSpinners()
        binding.search.setOnClickListener {
            displaySearchedBibleVerse()
        }

        binding.more.setOnClickListener {
            showPopupMenu(it, viewModel.searchedBibleVerse)
        }

        return binding.root
    }

    private fun initSpinners() {
        /** Books */
        val onBookSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                /** Set Chapter Range */
                coroutineScope.launch {
                    viewModel.bibleVerses = withContext(Dispatchers.IO) {
                        viewModel.bibleVerseDao.getBookValue(pos.inc())
                    }
                    val groupedBibleVerses = viewModel.bibleVerses.groupBy { it.chapter }
                    setChapterSpinner(groupedBibleVerses)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {  }
        }

        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.books,
            R.layout.item_spinner
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
            binding.bookSpinner.adapter = adapter
            binding.bookSpinner.onItemSelectedListener = onBookSelectedListener
        }
    }

    private fun setChapterSpinner(groupedBibleVerses: Map<Int, List<BibleVerse>>) {
        /** Chapters */
        val onChapterSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                /** Set Verse Range */
                setVerseSpinner(groupedBibleVerses[pos.inc()] ?: listOf())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {  }
        }

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner,
            groupedBibleVerses.keys.toTypedArray()
        )

        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        binding.chapterSpinner.adapter = spinnerAdapter
        binding.chapterSpinner.onItemSelectedListener = onChapterSelectedListener
    }

    private fun setVerseSpinner(bibleVerses: List<BibleVerse>) {
        /** Verses */
        val onVerseSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                /** Set searchedBibleVerse */
                viewModel.searchedBibleVerse = bibleVerses[pos]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {  }
        }

        val bibleVerseIndices = bibleVerses.map { it.verse }

        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner,
            bibleVerseIndices.toTypedArray()
        )

        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        binding.verseSpinner.adapter = spinnerAdapter
        binding.verseSpinner.onItemSelectedListener = onVerseSelectedListener
    }

    private fun displaySearchedBibleVerse() {
        val bibleVerse = viewModel.searchedBibleVerse
        val bibleVerseInformation = viewModel.books[bibleVerse.book.dec()] +
                " ${bibleVerse.chapter}장 ${bibleVerse.verse}절"

        binding.bibleVerseContainer.fadeOut(200) {
            binding.bibleVerseWord.text = bibleVerse.word
            binding.bibleVerseInformation.text = bibleVerseInformation

            binding.bibleVerseWord.setTextSize(TypedValue.COMPLEX_UNIT_SP, MainApplication.fontSize + 2F)
            binding.bibleVerseInformation.setTextSize(TypedValue.COMPLEX_UNIT_SP, MainApplication.fontSize)

            binding.more.fadeIn(200) {  }
            it.fadeIn(200) {
                binding.more.visibility = View.VISIBLE
            }
        }
    }

    private fun showPopupMenu(view: View, bibleVerse: BibleVerse) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.inflate(R.menu.menu_bible_verse)
        popupMenu.setOnMenuItemClickListener { item ->

            when (item.itemId) {
                R.id.addToFavorites -> {
                    BibleVerseUtil.addToFavorites(
                        coroutineScope,
                        viewModel.favoriteBibleVerseDao,
                        bibleVerse
                    )
                    showToast(getString(R.string.add_to_favorites_success))
                    true
                }
                R.id.copyToClipboard -> {
                    BibleVerseUtil.copyToClipboard(requireActivity(), bibleVerse.toText())
                    true
                }
                R.id.share -> {
                    BibleVerseUtil.share(requireActivity(), bibleVerse.toText())
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun BibleVerse.toText(): String {
        val bookString = viewModel.books[book.dec()]
        return "$bookString ${chapter}장 ${verse}절 \n" +
                word
    }
}