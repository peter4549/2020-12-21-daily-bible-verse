package com.duke.elliot.biblereadinghabits.daily_bible_verse

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
        val popularBibleVerseIndex = DailyBibleVerseUtil.getPopularBibleVerseIndex(application)

        withContext(Dispatchers.IO) {
            val popularBibleVerse = popularBibleVerseDao.getAllValue()[popularBibleVerseIndex]
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

    fun getDisplayFavoriteBibleVerses(): List<BibleVerse> = runBlocking {
        val displayBibleVerses = mutableListOf<BibleVerse>()
        val favoriteBibleVerseIndex = DailyBibleVerseUtil.getFavoriteBibleVerseIndex(application)

        withContext(Dispatchers.IO) {
            val favoriteBibleVerse = popularBibleVerseDao.getAllValue()[favoriteBibleVerseIndex]
            val book = favoriteBibleVerse.book
            val chapter = favoriteBibleVerse.chapter
            val verse = favoriteBibleVerse.verse
            val bibleVerses = bibleVerseDao.getBookChapter(book, chapter)

            for (i in verse - 1 until bibleVerses.size) {
                displayBibleVerses.add(bibleVerses[i])

                if (displayBibleVerses.count() > 4)
                    break
            }

            displayBibleVerses.toList()
        }
    }

    fun getDisplayRandomBibleVerses(): List<BibleVerse> = runBlocking {
        val displayBibleVerses = mutableListOf<BibleVerse>()

        withContext(Dispatchers.IO) {
            val randomBibleVerse = DailyBibleVerseUtil.getRandomBibleVerse(application, bibleVerseDao)

            val book = randomBibleVerse?.book ?: 1
            val chapter = randomBibleVerse?.chapter ?: 1
            val verse = randomBibleVerse?.verse ?: 1
            val bibleVerses = bibleVerseDao.getBookChapter(book, chapter)

            for (i in verse - 1 until bibleVerses.size) {
                displayBibleVerses.add(bibleVerses[i])

                if (displayBibleVerses.count() > 4)
                    break
            }

            displayBibleVerses.toList()
        }
    }

    fun getDisplayInOrderBibleVerses(): List<BibleVerse> = runBlocking {
        val displayBibleVerses = mutableListOf<BibleVerse>()

        withContext(Dispatchers.IO) {
            val inOrderBibleVerse = DailyBibleVerseUtil.getInOrderBibleVerseInformation(application, bibleVerseDao)

            val book = inOrderBibleVerse.book
            val chapter = inOrderBibleVerse.chapter
            val verse = inOrderBibleVerse.verse
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