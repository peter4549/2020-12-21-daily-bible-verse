package com.duke.elliot.biblereadinghabits.daily_bible_verses

import android.app.Application
import androidx.lifecycle.ViewModel
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.database.AppDatabase
import com.duke.elliot.biblereadinghabits.database.BibleVerse
import com.duke.elliot.biblereadinghabits.database.StaticAppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class DailyBibleVerseViewModel(private val application: Application): ViewModel() {
    private val staticAppDatabase = StaticAppDatabase.getInstanceFromAsset(application, "BibleVerses.db")
    private val appDatabase = AppDatabase.getInstance(application)
    val bibleVerseDao = staticAppDatabase.bibleVerseDao()
    val favoriteBibleVerseDao = appDatabase.favoriteBibleVerseDao()
    val popularBibleVerseDao = staticAppDatabase.popularBibleVerseDao()
    private val books = application.resources.getStringArray(R.array.books)

    fun getBook(index: Int): String = books[index.dec()]

    fun getDisplayPopularBibleVerses(): List<BibleVerse> = runBlocking {
        val displayBibleVerses = mutableListOf<BibleVerse>()
        val popularBibleVerseIndex = DailyBibleVerseUtil.getPopularBibleVersesIndex(application)

        withContext(Dispatchers.Default) {
            val popularBibleVerse = popularBibleVerseDao.getAllValues()[popularBibleVerseIndex]
            val book = popularBibleVerse.book
            val chapter = popularBibleVerse.chapter
            val verse = popularBibleVerse.verse
            val bibleVerses = bibleVerseDao.getBookChapter(book, chapter)

            for (i in verse - 1 until bibleVerses.size) {
                displayBibleVerses.add(bibleVerses[i])

                if (displayBibleVerses.count() > 4)
                    break
            }

            displayBibleVerses.toList()
        }
    }
}