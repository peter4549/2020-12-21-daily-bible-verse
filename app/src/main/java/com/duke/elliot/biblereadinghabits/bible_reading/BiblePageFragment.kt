package com.duke.elliot.biblereadinghabits.bible_reading

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.base.BaseFragment
import com.duke.elliot.biblereadinghabits.database.BibleVerse
import com.duke.elliot.biblereadinghabits.databinding.FragmentBiblePageBinding
import com.duke.elliot.biblereadinghabits.main.MainApplication
import com.duke.elliot.biblereadinghabits.util.fadeIn
import kotlinx.android.synthetic.main.item_bible_verse_word_only.view.*

class BiblePageFragment: BaseFragment() {
    private lateinit var binding: FragmentBiblePageBinding
    private lateinit var bibleVerses: ArrayList<BibleVerse>
    private lateinit var bibleVerseAdapter: BibleVerseAdapter
    private lateinit var books: Array<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bible_page, container, false)

        books = requireContext().resources.getStringArray(R.array.books)

        savedInstanceState?.let { bundle ->
            bundle.getParcelableArrayList<BibleVerse>(KEY_BIBLE_VERSES)?.let {
                if (it.isNotEmpty()) {
                    bibleVerses = it
                    setDisplayBibleVerses(bibleVerses)
                }
            } ?: run {

            }
        } ?: run {
            arguments?.getParcelableArrayList<BibleVerse>(BibleReadingFragment.KEY_BIBLE_VERSES)?.let {
                if (it.isNotEmpty()) {
                    bibleVerses = it
                    setDisplayBibleVerses(bibleVerses)
                }
            } ?: run {

            }
        }

        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (::bibleVerses.isInitialized)
            outState.putParcelableArrayList(KEY_BIBLE_VERSES, bibleVerses)
    }

    inner class BibleVerseAdapter(private val bibleVerses: List<BibleVerse>): RecyclerView.Adapter<BibleVerseAdapter.ViewHolder>() {
        inner class ViewHolder(val view: View): RecyclerView.ViewHolder(view)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_bible_verse_word_only, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val bibleVerse = bibleVerses[position]
            val verse = bibleVerse.verse
            val word = bibleVerse.word
            val text = "$verse $word"

            holder.view.word.text = text
            holder.view.word.setTextSize(TypedValue.COMPLEX_UNIT_SP, MainApplication.fontSize)
        }

        override fun getItemCount(): Int = bibleVerses.count()
    }

    private fun setDisplayBibleVerses(bibleVerses: List<BibleVerse>) {
        val bibleVerse = bibleVerses[0]
        val bookName = books[bibleVerse.book.dec()]
        val chapter = bibleVerse.chapter
        val bookAndChapterText = "$bookName ${chapter}장"

        bibleVerseAdapter = BibleVerseAdapter(bibleVerses)
        binding.bookAndChapter.text = bookAndChapterText
        binding.verseRecyclerView.adapter = bibleVerseAdapter

        binding.bookAndChapter.setTextSize(TypedValue.COMPLEX_UNIT_SP, MainApplication.fontSize + 2)
    }

    companion object {
        private const val KEY_BIBLE_VERSES = "com.duke.elliot.biblereadinghabits.bible_reading" +
                ".bible_page_fragment.key_bible_verses"
    }
}