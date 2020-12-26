package com.duke.elliot.biblereadinghabits.daily_bible_verses

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class DailyBibleVerseViewModelFactory(private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(DailyBibleVerseViewModel::class.java)) {
            return DailyBibleVerseViewModel(application) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}