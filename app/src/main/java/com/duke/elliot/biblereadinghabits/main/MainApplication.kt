package com.duke.elliot.biblereadinghabits.main

import android.app.Application
import com.duke.elliot.biblereadinghabits.daily_bible_verse.update.DailyBibleVerseUpdateManager
import com.duke.elliot.biblereadinghabits.util.ColorUtil
import timber.log.Timber

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        // TODO: Check night mode.
        // AppCompatDelegate.setDefaultNightMode(getNightMode(this))
        primaryThemeColor = ColorUtil.getPrimaryThemeColor(this)
        DailyBibleVerseUpdateManager.setUpdateDailyBibleVerseAlarm(this)
    }

    companion object {
        var primaryThemeColor = 0
    }
}