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

class DailyBibleVerseViewModel(application: Application): ViewModel() {
    private val staticAppDatabase = StaticAppDatabase.getInstanceFromAsset(application, "BibleVerses.db")
    private val appDatabase = AppDatabase.getInstance(application)
    val bibleVerseDao = staticAppDatabase.bibleVerseDao()
    val favoriteBibleVerseDao = appDatabase.favoriteBibleVerseDao()
    val popularBibleVerseDao = staticAppDatabase.popularBibleVerseDao()
    private val books = application.resources.getStringArray(R.array.books)

    fun getBook(index: Int): String = books[index.dec()]

    val displayDailyBibleVerses: List<BibleVerse> = when (DailyBibleVerseUtil.getRange(application)) {
        DailyBibleVerseUtil.POPULAR_BIBLE_VERSES -> DailyBibleVerseUtil.getDisplayPopularBibleVerses(
            application, bibleVerseDao, popularBibleVerseDao
        )
        DailyBibleVerseUtil.MY_BIBLE_VERSES -> DailyBibleVerseUtil.getDisplayFavoriteBibleVerses(
            application, bibleVerseDao, favoriteBibleVerseDao, popularBibleVerseDao
        )
        DailyBibleVerseUtil.IN_ORDER -> DailyBibleVerseUtil.getDisplayInOrderBibleVerses(
            application, bibleVerseDao
        )
        DailyBibleVerseUtil.RANDOM -> DailyBibleVerseUtil.getDisplayRandomBibleVerses(
            application, bibleVerseDao
        )
        else -> throw IllegalArgumentException("Invalid range.")
    }
}