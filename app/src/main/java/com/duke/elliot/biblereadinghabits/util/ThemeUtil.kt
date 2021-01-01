package com.duke.elliot.biblereadinghabits.util

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

object ThemeUtil {

    private const val PREFERENCES_THEME = "com.duke.elliot.biblereadinghabits.util" +
            ".theme_util.preferences_theme"
    private const val KEY_NIGHT_MODE = "com.duke.elliot.biblereadinghabits.util" +
            ".theme_util.key_night_mode"

    fun storeNightMode(context: Context, nightMode: Int) {
        val preferences = context.getSharedPreferences(PREFERENCES_THEME, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(KEY_NIGHT_MODE, nightMode)
        editor.apply()
    }

    fun restoreNightMode(context: Context): Int {
        val preferences = context.getSharedPreferences(PREFERENCES_THEME, Context.MODE_PRIVATE)
        return preferences.getInt(KEY_NIGHT_MODE, MODE_NIGHT_FOLLOW_SYSTEM)
    }
}