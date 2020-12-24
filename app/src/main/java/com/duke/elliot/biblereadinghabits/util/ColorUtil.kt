package com.duke.elliot.biblereadinghabits.util

import android.content.Context
import androidx.core.content.ContextCompat
import com.duke.elliot.biblereadinghabits.R

object ColorUtil {
    private const val PREFERENCES_COLOR = "com.duke.elliot.biblereadinghabits.util" +
            ".preferences_color"
    private const val KEY_PRIMARY_THEME_COLOR = "com.duke.elliot.biblereadinghabits.util" +
            ".key_primary_theme_color"

    fun getPrimaryThemeColor(context: Context): Int {
        val preferences = context.getSharedPreferences(PREFERENCES_COLOR, Context.MODE_PRIVATE)
        val defaultPrimaryThemeColor = ContextCompat.getColor(context, R.color.default_primary_theme)
        return preferences.getInt(KEY_PRIMARY_THEME_COLOR, defaultPrimaryThemeColor)
    }

    fun setPrimaryThemeColor(context: Context, primaryThemeColor: Int) {
        val preferences = context.getSharedPreferences(PREFERENCES_COLOR, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(KEY_PRIMARY_THEME_COLOR, primaryThemeColor)
        editor.apply()
    }
}