package com.duke.elliot.biblereadinghabits.favorite_bible_verses

import android.app.Application
import androidx.lifecycle.ViewModel
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.database.AppDatabase

class FavoriteBibleVersesViewModel(application: Application): ViewModel() {
    val favoriteBibleVerses = AppDatabase.getInstance(application).favoriteBibleVerseDao().getAll()
    private val books = application.resources.getStringArray(R.array.books)

    fun getBook(index: Int) = books[index.dec()]
}