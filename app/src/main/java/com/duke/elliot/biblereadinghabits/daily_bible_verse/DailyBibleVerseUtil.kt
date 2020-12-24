package com.duke.elliot.biblereadinghabits.daily_bible_verse

import android.content.Context
import com.duke.elliot.biblereadinghabits.database.AppDatabase
import com.duke.elliot.biblereadinghabits.database.BibleVerse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

object DailyBibleVerseUtil {

    private const val PREFERENCES_RANGE = "com.duke.elliot.bible_reading_habits.daily_bible_verse" +
            ".daily_bible_verse_util.preferences_range"
    private const val KEY_RANGE = "com.duke.elliot.bible_reading_habits.daily_bible_verse" +
            ".daily_bible_verse_util.key_range"
    private const val KEY_POPULAR_BIBLE_VERSE_INDEX = "com.duke.elliot.bible_reading_habits.daily_bible_verse" +
            ".daily_bible_verse_util.key_popular_bible_verse_index"

    const val POPULAR_BIBLE_VERSES = 0
    const val MY_BIBLE_VERSES = 1
    const val IN_ORDER = 2
    const val RANDOM = 3

    fun getRange(context: Context): Int {
        val preferences = context.getSharedPreferences(PREFERENCES_RANGE, Context.MODE_PRIVATE)
        return preferences.getInt(KEY_RANGE, POPULAR_BIBLE_VERSES)
    }

    fun setRange(context: Context, range: Int) {
        val preferences = context.getSharedPreferences(PREFERENCES_RANGE, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(KEY_RANGE, range)
        editor.apply()
    }

    fun getBook() {

    }

    fun setBook() {

    }

    fun getPopularBibleVersesIndex(context: Context): Int {
        val preferences = context.getSharedPreferences(PREFERENCES_RANGE, Context.MODE_PRIVATE)
        return preferences.getInt(KEY_POPULAR_BIBLE_VERSE_INDEX, 0)
    }

    fun increasePopularBibleVersesIndex(context: Context) {
        val preferences = context.getSharedPreferences(PREFERENCES_RANGE, Context.MODE_PRIVATE)
        val popularBibleVerseIndex = preferences.getInt(KEY_POPULAR_BIBLE_VERSE_INDEX, 0)
        val editor = preferences.edit()
        editor.putInt(KEY_POPULAR_BIBLE_VERSE_INDEX, popularBibleVerseIndex.inc())
        editor.apply()
    }
}