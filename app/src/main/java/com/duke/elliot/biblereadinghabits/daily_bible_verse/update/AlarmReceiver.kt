package com.duke.elliot.biblereadinghabits.daily_bible_verse.update

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.duke.elliot.biblereadinghabits.daily_bible_verse.DailyBibleVerseUtil
import com.duke.elliot.biblereadinghabits.database.AppDatabase

class AlarmReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val favoriteBibleVerseDao = AppDatabase.getInstance(context).favoriteBibleVerseDao()

        when (DailyBibleVerseUtil.getRange(context)) {
            DailyBibleVerseUtil.POPULAR_BIBLE_VERSES ->
                DailyBibleVerseUpdateManager.increasePopularBibleVerseIndex(context)
            DailyBibleVerseUtil.MY_BIBLE_VERSES ->
                DailyBibleVerseUpdateManager.increaseFavoriteBibleVerseIndex(context, favoriteBibleVerseDao)
            DailyBibleVerseUtil.RANDOM ->
                DailyBibleVerseUpdateManager.updateRandomBibleVerseIndex(context)
            DailyBibleVerseUtil.IN_ORDER ->
                DailyBibleVerseUpdateManager.increaseInOrderBibleVerseInformation(context)
        }

        // DailyBibleVerseUpdateManager.setUpdateDailyBibleVerseAlarm(context)
    }
}