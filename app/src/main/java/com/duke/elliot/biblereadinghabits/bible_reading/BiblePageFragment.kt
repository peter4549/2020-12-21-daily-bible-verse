package com.duke.elliot.biblereadinghabits.bible_reading

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.base.BaseFragment
import com.duke.elliot.biblereadinghabits.database.AppDatabase
import com.duke.elliot.biblereadinghabits.database.BibleVerse
import com.duke.elliot.biblereadinghabits.database.FavoriteBibleVerseDao
import com.duke.elliot.biblereadinghabits.databinding.FragmentBiblePageBinding
import com.duke.elliot.biblereadinghabits.main.MainApplication
import com.duke.elliot.biblereadinghabits.util.BibleVerseUtil
import kotlinx.android.synthetic.main.item_bible_verse_word_only.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class BiblePageFragment: BaseFragment() {
    private lateinit var binding: FragmentBiblePageBinding
    private lateinit var bibleVerses: ArrayList<BibleVerse>
    private lateinit var bibleVerseAdapter: BibleVerseAdapter
    private lateinit var books: Array<String>
    private lateinit var favoriteBibleVerseDao: FavoriteBibleVerseDao

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bible_page, container, false)
        books = requireContext().resources.getStringArray(R.array.books)
        favoriteBibleVerseDao = AppDatabase.getInstance(requireContext()).favoriteBibleVerseDao()

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

            holder.view.setOnLongClickListener {
                showPopupMenu(it, bibleVerse)
                true
            }
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

    private fun showPopupMenu(view: View, bibleVerse: BibleVerse) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.inflate(R.menu.menu_bible_verse)
        popupMenu.setOnMenuItemClickListener { item ->

            when (item.itemId) {
                R.id.addToFavorites -> {
                    BibleVerseUtil.addToFavorites(
                        coroutineScope,
                        favoriteBibleVerseDao,
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
        val bookString = books[book.dec()]
        return "$bookString ${chapter}장 ${verse}절 \n" +
                word
    }

    companion object {
        private const val KEY_BIBLE_VERSES = "com.duke.elliot.biblereadinghabits.bible_reading" +
                ".bible_page_fragment.key_bible_verses"
    }
}