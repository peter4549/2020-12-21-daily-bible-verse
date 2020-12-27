package com.duke.elliot.biblereadinghabits.daily_bible_verse.update

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import com.duke.elliot.biblereadinghabits.daily_bible_verse.DailyBibleVerseUtil
import com.duke.elliot.biblereadinghabits.database.FavoriteBibleVerseDao
import com.duke.elliot.biblereadinghabits.util.BIBLE_VERSE_COUNT
import com.duke.elliot.biblereadinghabits.util.POPULAR_BIBLE_VERSES_COUNT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

const val UPDATE_DAILY_BIBLE_VERSE_REQUEST_ID = 17

object DailyBibleVerseUpdateManager {

    fun setUpdateDailyBibleVerseAlarm(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).let {
            PendingIntent.getBroadcast(context, UPDATE_DAILY_BIBLE_VERSE_REQUEST_ID, it, FLAG_UPDATE_CURRENT)
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 2)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val calendarNow = Calendar.getInstance()

        if (calendar.before(calendarNow) || calendarNow.time == calendar.time)
            calendar.add(Calendar.DATE, 1)

        val packageManager = context.packageManager
        val deviceBootReceiver = ComponentName(context, DeviceBootReceiver::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                intent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                intent
            )
        }

        packageManager.setComponentEnabledSetting(
            deviceBootReceiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }

    fun increasePopularBibleVerseIndex(context: Context) {
        val preferences = context.getSharedPreferences(DailyBibleVerseUtil.PREFERENCES_RANGE, Context.MODE_PRIVATE)
        var popularBibleVerseIndex = preferences.getInt(DailyBibleVerseUtil.KEY_POPULAR_BIBLE_VERSE_INDEX, 0)
        val editor = preferences.edit()

        if (popularBibleVerseIndex.inc() >= POPULAR_BIBLE_VERSES_COUNT)
            popularBibleVerseIndex = 0
        else
            popularBibleVerseIndex += 1

        editor.putInt(DailyBibleVerseUtil.KEY_POPULAR_BIBLE_VERSE_INDEX, popularBibleVerseIndex)
        editor.apply()
    }

    fun increaseFavoriteBibleVerseIndex(context: Context, favoriteBibleVerseDao: FavoriteBibleVerseDao) {
        val preferences = context.getSharedPreferences(DailyBibleVerseUtil.PREFERENCES_RANGE, Context.MODE_PRIVATE)
        var favoriteBibleVerseIndex = preferences.getInt(DailyBibleVerseUtil.KEY_FAVORITE_BIBLE_VERSE_INDEX, 0)
        val editor = preferences.edit()

        CoroutineScope(Dispatchers.IO).launch {
            val favoriteBibleVerseCount = favoriteBibleVerseDao.getAllValue().count()

            if (favoriteBibleVerseIndex.inc() >= favoriteBibleVerseCount)
                favoriteBibleVerseIndex = 0
            else
                favoriteBibleVerseIndex += 1

            editor.putInt(DailyBibleVerseUtil.KEY_FAVORITE_BIBLE_VERSE_INDEX, favoriteBibleVerseIndex)
            editor.apply()
        }
    }

    fun updateRandomBibleVerseIndex(context: Context) {
        val preferences = context.getSharedPreferences(DailyBibleVerseUtil.PREFERENCES_RANGE, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val randomBibleVerseIndex = (0..BIBLE_VERSE_COUNT.dec()).random()
        editor.putInt(DailyBibleVerseUtil.KEY_RANDOM_BIBLE_VERSE_INDEX, randomBibleVerseIndex)
        editor.apply()
    }

    fun increaseInOrderBibleVerseInformation(context: Context) {
        val preferences = context.getSharedPreferences(DailyBibleVerseUtil.PREFERENCES_RANGE, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        var inOrderBibleVerseIndex = preferences.getInt(DailyBibleVerseUtil.KEY_IN_ORDER_BIBLE_VERSE_INDEX, 0)

        if (inOrderBibleVerseIndex.inc() >= BIBLE_VERSE_COUNT)
            inOrderBibleVerseIndex = 0
        else
            inOrderBibleVerseIndex += 1

        editor.putInt(DailyBibleVerseUtil.KEY_IN_ORDER_BIBLE_VERSE_INDEX, inOrderBibleVerseIndex)
        editor.apply()
    }
}