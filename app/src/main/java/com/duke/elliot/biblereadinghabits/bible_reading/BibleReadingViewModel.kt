package com.duke.elliot.biblereadinghabits.bible_reading

import android.app.Application
import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.database.AppDatabase
import com.duke.elliot.biblereadinghabits.database.BibleVerseInformation
import com.duke.elliot.biblereadinghabits.database.StaticAppDatabase
import kotlinx.android.parcel.Parcelize

class BibleReadingViewModel(private val application: Application): ViewModel() {

    private val bibleVerseDao = StaticAppDatabase.getInstance(application).bibleVerseDao()
    val lastBibleVerseInformation = getLastBibleVerseRead()

    fun getBook(index: Int) = bibleVerseDao.getBook(index)

    private fun getLastBibleVerseRead(): BibleVerseInformation {
        val preferences = application.getSharedPreferences(
            PREFERENCES_LAST_BIBLE_VERSE_READ,
            Context.MODE_PRIVATE
        )
        val book = preferences.getInt(KEY_LAST_BOOK_READ, 1)
        val chapter = preferences.getInt(KEY_LAST_CHAPTER_READ, 1)
        val verse = preferences.getInt(KEY_LAST_VERSE_READ, 1)

        return BibleVerseInformation(book, chapter, verse)
    }

    companion object {
        private const val PREFERENCES_LAST_BIBLE_VERSE_READ = "com.duke.elliot.biblereadinghabits.bible_reading" +
                ".bible_reading_view_model.preferences_last_bible_verse_read"
        private const val KEY_LAST_BOOK_READ = "com.duke.elliot.biblereadinghabits.bible_reading" +
                ".bible_reading_view_model.key_last_book_read"
        private const val KEY_LAST_CHAPTER_READ = "com.duke.elliot.biblereadinghabits.bible_reading" +
                ".bible_reading_view_model.key_last_chapter_read"
        private const val KEY_LAST_VERSE_READ = "com.duke.elliot.biblereadinghabits.bible_reading" +
                ".bible_reading_view_model.key_last_verse_read"
    }
}