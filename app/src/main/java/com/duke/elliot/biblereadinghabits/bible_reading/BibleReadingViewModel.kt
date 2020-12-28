package com.duke.elliot.biblereadinghabits.bible_reading

import android.app.Application
import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.database.AppDatabase
import com.duke.elliot.biblereadinghabits.database.BibleVerse
import com.duke.elliot.biblereadinghabits.database.BibleVerseInformation
import com.duke.elliot.biblereadinghabits.database.StaticAppDatabase
import kotlinx.android.parcel.Parcelize

class BibleReadingViewModel(private val application: Application, initBookPage: BookPage?): ViewModel() {
    lateinit var firstBibleVerseOnCurrentPage: BibleVerse
    val bibleVerseDao = StaticAppDatabase.getInstance(application).bibleVerseDao()
    val books = application.resources.getStringArray(R.array.books)
    var currentBookPage = initBookPage ?: getLastBookPageRead()
    var currentBook = MutableLiveData(currentBookPage.book)

    private fun getLastBookPageRead(): BookPage {
        val preferences = application.getSharedPreferences(
            PREFERENCES_LAST_BIBLE_VERSE_READ,
            Context.MODE_PRIVATE
        )
        val book = preferences.getInt(KEY_LAST_BOOK_READ, 1)
        val page = preferences.getInt(KEY_LAST_PAGE_READ, 0)

        return BookPage(
            book = book,
            page = page
        )
    }

    fun saveLastBookPageRead() {
        val preferences = application.getSharedPreferences(
            PREFERENCES_LAST_BIBLE_VERSE_READ,
            Context.MODE_PRIVATE
        )
        val editor = preferences.edit()
        editor.putInt(KEY_LAST_BOOK_READ, currentBookPage.book)
        editor.putInt(KEY_LAST_PAGE_READ, currentBookPage.page)
        editor.apply()
    }

    companion object {
        private const val PREFERENCES_LAST_BIBLE_VERSE_READ = "com.duke.elliot.biblereadinghabits.bible_reading" +
                ".bible_reading_view_model.preferences_last_bible_verse_read"
        private const val KEY_LAST_BOOK_READ = "com.duke.elliot.biblereadinghabits.bible_reading" +
                ".bible_reading_view_model.key_last_book_read"
        private const val KEY_LAST_PAGE_READ = "com.duke.elliot.biblereadinghabits.bible_reading" +
                ".bible_reading_view_model.key_last_page_read"
    }
}

