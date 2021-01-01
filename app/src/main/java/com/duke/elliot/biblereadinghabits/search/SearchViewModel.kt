package com.duke.elliot.biblereadinghabits.search

import android.app.Application
import androidx.lifecycle.ViewModel
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.database.AppDatabase
import com.duke.elliot.biblereadinghabits.database.BibleVerse
import com.duke.elliot.biblereadinghabits.database.StaticAppDatabase

class SearchViewModel(application: Application): ViewModel() {
    val bibleVerseDao = StaticAppDatabase
        .getInstanceFromAsset(application, "BibleVerses.db")
        .bibleVerseDao()
    val favoriteBibleVerseDao = AppDatabase.getInstance(application).favoriteBibleVerseDao()
    val books: Array<String> = application.resources.getStringArray(R.array.books)
    lateinit var bibleVerses: List<BibleVerse>
    lateinit var searchedBibleVerse: BibleVerse
}