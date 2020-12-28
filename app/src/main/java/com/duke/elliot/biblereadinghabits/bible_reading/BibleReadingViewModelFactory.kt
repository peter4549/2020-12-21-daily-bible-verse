package com.duke.elliot.biblereadinghabits.bible_reading

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class BibleReadingViewModelFactory(private val application: Application, private val initBookPage: BookPage?):
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(BibleReadingViewModel::class.java)) {
            return BibleReadingViewModel(application, initBookPage) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}