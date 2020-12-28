package com.duke.elliot.biblereadinghabits.main

import android.app.Application
import com.duke.elliot.biblereadinghabits.daily_bible_verse.drawer.DEFAULT_FONT_SIZE
import com.duke.elliot.biblereadinghabits.daily_bible_verse.drawer.DrawerMenuUtil
import com.duke.elliot.biblereadinghabits.daily_bible_verse.update.DailyBibleVerseUpdateManager
import timber.log.Timber

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        // TODO: Check night mode.
        // AppCompatDelegate.setDefaultNightMode(getNightMode(this))
        primaryThemeColor = DrawerMenuUtil.restoreThemeColor(this)
        fontSize = DrawerMenuUtil.restoreFontSize(this)
        DailyBibleVerseUpdateManager.setUpdateDailyBibleVerseAlarm(this)
    }

    companion object {
        var primaryThemeColor = 0
        var fontSize = DEFAULT_FONT_SIZE
    }
}