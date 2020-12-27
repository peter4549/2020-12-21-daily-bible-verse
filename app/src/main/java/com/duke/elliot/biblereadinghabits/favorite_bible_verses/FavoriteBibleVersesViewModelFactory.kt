package com.duke.elliot.biblereadinghabits.favorite_bible_verses

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FavoriteBibleVersesViewModelFactory(private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        if (modelClass.isAssignableFrom(FavoriteBibleVersesViewModel::class.java)) {
            return FavoriteBibleVersesViewModel(application) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}