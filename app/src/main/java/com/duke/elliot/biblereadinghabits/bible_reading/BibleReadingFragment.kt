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
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.base.BaseFragment
import com.duke.elliot.biblereadinghabits.database.BibleVerse
import com.duke.elliot.biblereadinghabits.databinding.FragmentBibleReadingBinding
import com.wajahatkarim3.easyflipviewpager.BookFlipPageTransformer2

class BibleReadingFragment: BaseFragment() {

    private lateinit var binding: FragmentBibleReadingBinding
    private lateinit var viewModel: BibleReadingViewModel

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
        val viewModelFactory = BibleReadingViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[BibleReadingViewModel::class.java]

        viewModel.getBook(viewModel.lastBibleVerseInformation.book).observe(
            viewLifecycleOwner, Observer { bibleVerses ->
                val displayBibleVerses = mutableListOf<List<BibleVerse>>()
                val groupedBibleVerses = bibleVerses.groupBy { it.chapter }

                val keys = groupedBibleVerses.keys.sorted()
                for (key in keys) {
                    val values = groupedBibleVerses[key] ?: continue
                    val sortedValues = values.sortedBy { it.verse }
                    displayBibleVerses.addAll(sortedValues.chunked(5))
                }

                val bibleVersesAdapter = BibleVersesAdapter(requireActivity(), displayBibleVerses)
                binding.bibleVerseViewPager2.adapter = bibleVersesAdapter

                val bookFlipPageTransformer = BookFlipPageTransformer2()
                bookFlipPageTransformer.isEnableScale = true
                bookFlipPageTransformer.scaleAmountPercent = 5F
                binding.bibleVerseViewPager2.offscreenPageLimit = OFFSCREEN_PAGE_LIMIT_DEFAULT
                binding.bibleVerseViewPager2.setPageTransformer(bookFlipPageTransformer)
            })

        return binding.root
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
    }

    companion object {
        const val KEY_BIBLE_VERSES = "com.duke.elliot.biblereadinghabits.bible_reading" +
                ".bible_reading_fragment.key_bible_verses"
    }
}