package com.duke.elliot.biblereadinghabits.app_widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.daily_bible_verse.DailyBibleVerseUtil
import com.duke.elliot.biblereadinghabits.database.AppDatabase
import com.duke.elliot.biblereadinghabits.database.BibleVerse
import com.duke.elliot.biblereadinghabits.database.StaticAppDatabase
import com.duke.elliot.biblereadinghabits.main.MainActivity
import com.duke.elliot.biblereadinghabits.splash.SplashActivity
import java.lang.IllegalArgumentException

class BibleVerseAppWidgetProvider: AppWidgetProvider() {

    private lateinit var books: Array<String>

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)

        val appDatabase = AppDatabase.getInstance(context)
        val staticAppDatabase = StaticAppDatabase.getInstanceFromAsset(context, "BibleVerses.db")
        val bibleVerseDao = staticAppDatabase.bibleVerseDao()
        val favoriteBibleVerseDao = appDatabase.favoriteBibleVerseDao()
        val popularBibleVerseDao = staticAppDatabase.popularBibleVerseDao()

        books = context.resources.getStringArray(R.array.books)

        val bibleVerse: BibleVerse = when (DailyBibleVerseUtil.getRange(context)) {
            DailyBibleVerseUtil.POPULAR_BIBLE_VERSES -> DailyBibleVerseUtil.getDisplayPopularBibleVerses(
                context, bibleVerseDao, popularBibleVerseDao
            )[0]
            DailyBibleVerseUtil.MY_BIBLE_VERSES -> DailyBibleVerseUtil.getDisplayFavoriteBibleVerses(
                context, bibleVerseDao, favoriteBibleVerseDao, popularBibleVerseDao
            )[0]
            DailyBibleVerseUtil.IN_ORDER -> DailyBibleVerseUtil.getDisplayInOrderBibleVerses(
                context, bibleVerseDao
            )[0]
            DailyBibleVerseUtil.RANDOM -> DailyBibleVerseUtil.getDisplayRandomBibleVerses(
                context, bibleVerseDao
            )[0]
            else -> throw IllegalArgumentException("Invalid range.")
        }

        appWidgetIds?.forEach { appWidgetId ->
            val views: RemoteViews = bind(context, bibleVerse)
            appWidgetManager?.updateAppWidget(appWidgetId, views)
        }
    }

    private fun bind(context: Context?, bibleVerse: BibleVerse): RemoteViews {
        val bibleVerseInformation = "${books[bibleVerse.book.dec()]} ${bibleVerse.chapter}장 ${bibleVerse.verse}절"
        val views = RemoteViews(context?.packageName, R.layout.layout_app_widget)
        views.setOnClickPendingIntent(R.id.bibleVerseContainer, createLaunchAppPendingIntent(context))
        views.setCharSequence(R.id.bibleVerseWord, "setText", bibleVerse.word)
        views.setCharSequence(R.id.bibleVerseInformation, "setText", bibleVerseInformation)

        return views
    }

    private fun createLaunchAppPendingIntent(context: Context?): PendingIntent {
        val intent = Intent(context, SplashActivity::class.java)
        intent.setAction(Intent.ACTION_MAIN)
            .addCategory(Intent.CATEGORY_LAUNCHER)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        return intent.let {
            PendingIntent.getActivity(context, 0, it, 0)
        }
    }
}