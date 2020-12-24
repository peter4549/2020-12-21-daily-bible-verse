package com.duke.elliot.biblereadinghabits.bible_reading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.base.BaseFragment
import com.duke.elliot.biblereadinghabits.database.BibleVerse
import com.duke.elliot.biblereadinghabits.databinding.FragmentBiblePageBinding
import kotlinx.android.synthetic.main.item_bible_verse.view.*

class BiblePageFragment: BaseFragment() {
    private lateinit var binding: FragmentBiblePageBinding
    private lateinit var bibleVerseAdapter: BibleVerseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bible_page, container, false)

        savedInstanceState?.let {

        } ?: run {
            arguments?.getParcelableArrayList<BibleVerse>(BibleReadingFragment.KEY_BIBLE_VERSES)?.let {
                bibleVerseAdapter = BibleVerseAdapter(it)
                binding.verseRecyclerView.adapter = bibleVerseAdapter
            } ?: run {

            }
        }

        return binding.root
    }

    inner class BibleVerseAdapter(private val bibleVerses: List<BibleVerse>): RecyclerView.Adapter<BibleVerseAdapter.ViewHolder>() {
        inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bible_verse, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val bibleVerse = bibleVerses[position]
            val verse = bibleVerse.verse
            val word = bibleVerse.word
            val text = "$verse $word"

            holder.view.word.text = text
        }

        override fun getItemCount(): Int = bibleVerses.count()
    }
}