package com.duke.elliot.biblereadinghabits.bible_reading

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BibleReadingViewModelFactory(private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(BibleReadingViewModel::class.java)) {
            return BibleReadingViewModel(application) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}