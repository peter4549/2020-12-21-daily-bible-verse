package com.duke.elliot.biblereadinghabits.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.database.BibleVerse
import com.duke.elliot.biblereadinghabits.database.FavoriteBibleVerse
import com.duke.elliot.biblereadinghabits.database.FavoriteBibleVerseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.Exception

private const val CLIP_DATA_LABEL = "com.duke.elliot.biblereadinghabits.util" +
        ".bible_verse_util"

object BibleVerseUtil {

    fun copyToClipboard(activity: Activity, text: String) {
        try {
            val clipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText(CLIP_DATA_LABEL, text)
            clipboard.setPrimaryClip(clipData)

            Toast.makeText(activity,
                activity.getString(R.string.copy_to_clipboard_success),
                Toast.LENGTH_LONG
            ).show()
        } catch(e: Exception) {
            Timber.e(e)
            Toast.makeText(activity,
                activity.getString(R.string.copy_to_clipboard_failed_message),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun share(activity: Activity, text: String) {
        val intent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }

        activity.startActivity(Intent.createChooser(intent, null))
    }

    fun addToFavorites(coroutineScope: CoroutineScope,
                       favoriteBibleVerseDao: FavoriteBibleVerseDao,
                       bibleVerse: BibleVerse) {
        coroutineScope.launch(Dispatchers.IO) {
            val favoriteBibleVerse = FavoriteBibleVerse(
                book = bibleVerse.book,
                chapter = bibleVerse.chapter,
                verse = bibleVerse.verse,
                word = bibleVerse.word,
                addedTime = System.currentTimeMillis()
            )

            favoriteBibleVerseDao.insert(favoriteBibleVerse)

        }
    }

    fun deleteFromFavorites(coroutineScope: CoroutineScope,
                            favoriteBibleVerseDao: FavoriteBibleVerseDao,
                            favoriteBibleVerse: FavoriteBibleVerse) {
        coroutineScope.launch(Dispatchers.IO) {
            favoriteBibleVerseDao.delete(favoriteBibleVerse)
        }
    }
}