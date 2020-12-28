package com.duke.elliot.biblereadinghabits.daily_bible_verse.update

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DeviceBootReceiver : BroadcastReceiver()  {

    override fun onReceive(context: Context, intent: Intent) {
        /*
        if (intent.action == "android.intent.action.BOOT_COMPLETED")
            DailyBibleVerseUpdateManager.setUpdateDailyBibleVerseAlarm(context)
         */
    }
}