package com.duke.elliot.biblereadinghabits.favorite_bible_verses

import android.app.Application
import androidx.lifecycle.ViewModel
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.database.AppDatabase

class FavoriteBibleVersesViewModel(application: Application): ViewModel() {
    val favoriteBibleVerseDao = AppDatabase.getInstance(application).favoriteBibleVerseDao()
    val favoriteBibleVerses = favoriteBibleVerseDao.getAll()
    private val books = application.resources.getStringArray(R.array.books)

    fun getBook(index: Int): String = books[index.dec()]
}