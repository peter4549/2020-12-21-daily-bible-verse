package com.duke.elliot.biblereadinghabits.search

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.duke.elliot.biblereadinghabits.bible_reading.BibleReadingViewModel
import com.duke.elliot.biblereadinghabits.bible_reading.BookPage

class SearchViewModelFactory(private val application: Application):
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
            return SearchViewModel(application) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}