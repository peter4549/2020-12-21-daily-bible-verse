package com.duke.elliot.biblereadinghabits.main

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.duke.elliot.biblereadinghabits.daily_bible_verse.drawer.DEFAULT_FONT_SIZE
import com.duke.elliot.biblereadinghabits.daily_bible_verse.drawer.DrawerMenuUtil
import com.duke.elliot.biblereadinghabits.daily_bible_verse.update.DailyBibleVerseUpdateManager
import com.duke.elliot.biblereadinghabits.util.ThemeUtil.restoreNightMode
import timber.log.Timber

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        AppCompatDelegate.setDefaultNightMode(restoreNightMode(this))
        primaryThemeColor = DrawerMenuUtil.restoreThemeColor(this)
        fontSize = DrawerMenuUtil.restoreFontSize(this)
        DailyBibleVerseUpdateManager.setUpdateDailyBibleVerseAlarm(this)
    }

    companion object {
        var primaryThemeColor = 0
        var fontSize = DEFAULT_FONT_SIZE
    }
}