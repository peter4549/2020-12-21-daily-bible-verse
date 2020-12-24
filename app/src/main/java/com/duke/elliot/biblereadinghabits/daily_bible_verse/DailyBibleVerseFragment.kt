package com.duke.elliot.biblereadinghabits.daily_bible_verse

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.base.BaseFragment
import com.duke.elliot.biblereadinghabits.daily_bible_verse.DailyBibleVerseUtil.IN_ORDER
import com.duke.elliot.biblereadinghabits.daily_bible_verse.DailyBibleVerseUtil.MY_BIBLE_VERSES
import com.duke.elliot.biblereadinghabits.daily_bible_verse.DailyBibleVerseUtil.POPULAR_BIBLE_VERSES
import com.duke.elliot.biblereadinghabits.daily_bible_verse.DailyBibleVerseUtil.RANDOM
import com.duke.elliot.biblereadinghabits.database.BibleVerse
import com.duke.elliot.biblereadinghabits.databinding.FragmentDailyBibleVerseDrawerBinding
import com.duke.elliot.biblereadinghabits.main.MainApplication
import kotlinx.coroutines.*

class DailyBibleVerseFragment: BaseFragment() {

    private lateinit var binding: FragmentDailyBibleVerseDrawerBinding
    private lateinit var viewModel: DailyBibleVerseViewModel

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_daily_bible_verse_drawer, container, false)

        initDrawer()

        setOptionsMenu(R.menu.menu_daily_bible_verse_fragment)
        setOnBackPressedCallback {
            requireActivity().finish()
        }

        setOnOptionsItemSelectedListeners(
            R.id.settings to {
                showToast("Setting selected.")
            }
        )

        val viewModelFactory = DailyBibleVerseViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[DailyBibleVerseViewModel::class.java]

        when (DailyBibleVerseUtil.getRange(requireContext())) {
            POPULAR_BIBLE_VERSES -> {
                setDisplayBibleVerses(viewModel.getDisplayPopularBibleVerses())
            }
            MY_BIBLE_VERSES -> {}
            IN_ORDER -> {}
            RANDOM -> {}
        }

        binding.fragmentDailyBibleVerse.readBible.setOnClickListener {
            findNavController().navigate(
                DailyBibleVerseFragmentDirections
                    .actionDailyBibleVerseFragmentToBibleReadingFragment()
            )
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setColor()
    }

    private fun initDrawer() {
        val toggle = ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            binding.fragmentDailyBibleVerse.toolbar,
            R.string.open,
            R.string.close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
    }

    private fun setDisplayBibleVerses(bibleVerses: List<BibleVerse>) {
        val bibleVerse = bibleVerses[0]
        val bibleVerseInformation = "${viewModel.getBook(bibleVerse.book)} ${bibleVerse.chapter}장 ${bibleVerse.verse}절"
        binding.fragmentDailyBibleVerse.dailyBibleVerseWord.text = bibleVerse.word
        binding.fragmentDailyBibleVerse.dailyBibleVerseInformation.text = bibleVerseInformation
    }

    private fun setColor() {
        binding.fragmentDailyBibleVerse.readBible.backgroundTintList = ColorStateList.valueOf(MainApplication.primaryThemeColor)
    }
}