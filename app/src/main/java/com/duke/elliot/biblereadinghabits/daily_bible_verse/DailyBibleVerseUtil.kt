package com.duke.elliot.biblereadinghabits.daily_bible_verse

import android.content.Context
import com.duke.elliot.biblereadinghabits.database.BibleVerse
import com.duke.elliot.biblereadinghabits.database.BibleVerseDao
import com.duke.elliot.biblereadinghabits.database.FavoriteBibleVerseDao
import com.duke.elliot.biblereadinghabits.database.PopularBibleVerseDao
import com.duke.elliot.biblereadinghabits.util.BIBLE_VERSE_COUNT
import kotlinx.coroutines.*

object DailyBibleVerseUtil {

    const val PREFERENCES_RANGE = "com.duke.elliot.bible_reading_habits.daily_bible_verse" +
            ".daily_bible_verse_util.preferences_range"
    private const val KEY_RANGE = "com.duke.elliot.bible_reading_habits.daily_bible_verse" +
            ".daily_bible_verse_util.key_range"
    const val KEY_POPULAR_BIBLE_VERSE_INDEX = "com.duke.elliot.bible_reading_habits.daily_bible_verse" +
            ".daily_bible_verse_util.key_popular_bible_verse_index"
    const val KEY_FAVORITE_BIBLE_VERSE_INDEX = "com.duke.elliot.bible_reading_habits.daily_bible_verse" +
            ".daily_bible_verse_util.key_favorite_bible_verse_index"
    const val KEY_RANDOM_BIBLE_VERSE_INDEX = "com.duke.elliot.bible_reading_habits.daily_bible_verse" +
            ".daily_bible_verse_util.key_random_bible_verse_index"
    const val KEY_IN_ORDER_BIBLE_VERSE_INDEX = "com.duke.elliot.bible_reading_habits.daily_bible_verse" +
            ".daily_bible_verse_util.key_in_order_bible_verse_index"

    const val POPULAR_BIBLE_VERSES = 0
    const val MY_BIBLE_VERSES = 1
    const val RANDOM = 2
    const val IN_ORDER = 3

    fun getRange(context: Context): Int {
        val preferences = context.getSharedPreferences(PREFERENCES_RANGE, Context.MODE_PRIVATE)
        return preferences.getInt(KEY_RANGE, POPULAR_BIBLE_VERSES)
    }

    fun setRange(context: Context, range: Int) {
        val preferences = context.getSharedPreferences(PREFERENCES_RANGE, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putInt(KEY_RANGE, range)
        editor.apply()
    }

    private fun getPopularBibleVerseIndex(context: Context): Int {
        val preferences = context.getSharedPreferences(PREFERENCES_RANGE, Context.MODE_PRIVATE)
        return preferences.getInt(KEY_POPULAR_BIBLE_VERSE_INDEX, 0)
    }

    private fun getFavoriteBibleVerseIndex(context: Context): Int {
        val preferences = context.getSharedPreferences(PREFERENCES_RANGE, Context.MODE_PRIVATE)
        return preferences.getInt(KEY_FAVORITE_BIBLE_VERSE_INDEX, 0)
    }

    private fun getRandomBibleVerse(context: Context, bibleVerseDao: BibleVerseDao): BibleVerse = runBlocking {
        val preferences = context.getSharedPreferences(PREFERENCES_RANGE, Context.MODE_PRIVATE)
        val editor = preferences.edit()
        val randomBibleVerseIndex = preferences.getInt(KEY_RANDOM_BIBLE_VERSE_INDEX, (0..BIBLE_VERSE_COUNT.dec()).random())

        withContext(Dispatchers.IO) {
            editor.putInt(KEY_RANDOM_BIBLE_VERSE_INDEX, randomBibleVerseIndex)
            editor.apply()
            bibleVerseDao.getAllValue()[randomBibleVerseIndex]
        }
    }

    private fun getInOrderBibleVerseInformation(context: Context, bibleVerseDao: BibleVerseDao): BibleVerse = runBlocking {
        val preferences = context.getSharedPreferences(PREFERENCES_RANGE, Context.MODE_PRIVATE)
        val inOrderBibleVerseIndex = preferences.getInt(KEY_IN_ORDER_BIBLE_VERSE_INDEX, 0)

        withContext(Dispatchers.IO) {
            val bibleVerses = bibleVerseDao.getAllValue()
            bibleVerses[inOrderBibleVerseIndex]
        }
    }

    fun getDisplayPopularBibleVerses(
        context: Context,
        bibleVerseDao: BibleVerseDao,
        popularBibleVerseDao: PopularBibleVerseDao
    ): List<BibleVerse> = runBlocking {
        val displayBibleVerses = mutableListOf<BibleVerse>()
        val popularBibleVerseIndex = getPopularBibleVerseIndex(context)

        withContext(Dispatchers.IO) {
            val popularBibleVerse = popularBibleVerseDao.getAllValue()[popularBibleVerseIndex]
            val book = popularBibleVerse.book
            val chapter = popularBibleVerse.chapter
            val verse = popularBibleVerse.verse
            val bibleVerses = bibleVerseDao.getBookChapter(book, chapter)

            for (i in verse - 1 until bibleVerses.size) {
                displayBibleVerses.add(bibleVerses[i])

                if (displayBibleVerses.count() > 4)
                    break
            }

            displayBibleVerses.toList()
        }
    }

    fun getDisplayFavoriteBibleVerses(
        context: Context,
        bibleVerseDao: BibleVerseDao,
        favoriteBibleVerseDao: FavoriteBibleVerseDao,
        popularBibleVerseDao: PopularBibleVerseDao
    ): List<BibleVerse> = runBlocking {
        val displayBibleVerses = mutableListOf<BibleVerse>()
        val favoriteBibleVerseIndex = getFavoriteBibleVerseIndex(context)

        withContext(Dispatchers.IO) {
            val favoriteBibleVerses = favoriteBibleVerseDao.getAllValue()
            if (favoriteBibleVerses.isEmpty())
                return@withContext getDisplayPopularBibleVerses(context, bibleVerseDao, popularBibleVerseDao)

            val favoriteBibleVerse = favoriteBibleVerses[favoriteBibleVerseIndex]
            val book = favoriteBibleVerse.book
            val chapter = favoriteBibleVerse.chapter
            val verse = favoriteBibleVerse.verse
            val bibleVerses = bibleVerseDao.getBookChapter(book, chapter)

            for (i in verse - 1 until bibleVerses.size) {
                displayBibleVerses.add(bibleVerses[i])

                if (displayBibleVerses.count() > 4)
                    break
            }

            displayBibleVerses.toList()
        }
    }

    fun getDisplayRandomBibleVerses(context: Context, bibleVerseDao: BibleVerseDao): List<BibleVerse> = runBlocking {
        val displayBibleVerses = mutableListOf<BibleVerse>()

        withContext(Dispatchers.IO) {
            val randomBibleVerse = getRandomBibleVerse(context, bibleVerseDao)

            val book = randomBibleVerse.book
            val chapter = randomBibleVerse.chapter
            val verse = randomBibleVerse.verse
            val bibleVerses = bibleVerseDao.getBookChapter(book, chapter)

            for (i in verse - 1 until bibleVerses.size) {
                displayBibleVerses.add(bibleVerses[i])

                if (displayBibleVerses.count() > 4)
                    break
            }

            displayBibleVerses.toList()
        }
    }

    fun getDisplayInOrderBibleVerses(context: Context, bibleVerseDao: BibleVerseDao): List<BibleVerse> = runBlocking {
        val displayBibleVerses = mutableListOf<BibleVerse>()

        withContext(Dispatchers.IO) {
            val inOrderBibleVerse = getInOrderBibleVerseInformation(context, bibleVerseDao)

            val book = inOrderBibleVerse.book
            val chapter = inOrderBibleVerse.chapter
            val verse = inOrderBibleVerse.verse
            val bibleVerses = bibleVerseDao.getBookChapter(book, chapter)

            for (i in verse - 1 until bibleVerses.size) {
                displayBibleVerses.add(bibleVerses[i])

                if (displayBibleVerses.count() > 4)
                    break
            }

            displayBibleVerses.toList()
        }
    }
}