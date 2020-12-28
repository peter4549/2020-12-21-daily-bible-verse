package com.duke.elliot.biblereadinghabits.bible_reading.bookmark

import android.content.Context
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.bible_reading.BookPage
import com.duke.elliot.biblereadinghabits.database.BibleVerse

object BibleBookmarkUtil {
    private const val PREFERENCES_BOOKMARK = "com.duke.elliot.biblereadinghabits.bible_reading" +
            ".bible_reading_view_model.preferences_bookmark"
    private const val KEY_BOOKMARKS = "com.duke.elliot.biblereadinghabits.bible_reading" +
            ".bible_reading_view_model.key_bookmark"

    fun getBookmarks(context: Context): List<Pair<BookPage, String>> {
        val list = mutableListOf<Pair<BookPage, String>>()
        val preferences = context.getSharedPreferences(PREFERENCES_BOOKMARK, Context.MODE_PRIVATE)
        val bookmarks = preferences.getStringSet(KEY_BOOKMARKS, setOf()) ?: setOf()
        val sortedBookmarks = bookmarks.toList().sorted()

        sortedBookmarks.forEach { bookmark ->
            list.add(parseBookmarkStringToBookPageTextPair(context, bookmark))
        }

        return list
    }

    fun addToBookmarks(context: Context, currentBookPage: BookPage, firstBibleVerseOnCurrentPage: BibleVerse) {
        val preferences = context.getSharedPreferences(PREFERENCES_BOOKMARK, Context.MODE_PRIVATE)
        val bookmarks = preferences.getStringSet(KEY_BOOKMARKS, mutableSetOf()) ?: mutableSetOf()
        bookmarks.add(parseBibleVerseToBookmarkString(currentBookPage, firstBibleVerseOnCurrentPage))

        val editor = preferences.edit()
        editor.putStringSet(KEY_BOOKMARKS, bookmarks)
        editor.apply()
    }

    fun deleteFromBookmarks(context: Context, position: Int) {
        val preferences = context.getSharedPreferences(PREFERENCES_BOOKMARK, Context.MODE_PRIVATE)
        val bookmarks = preferences.getStringSet(KEY_BOOKMARKS, mutableSetOf()) ?: mutableSetOf()
        val bookmarkList = bookmarks.toList().sorted().toMutableList()
        bookmarkList.removeAt(position)
        val bookmarkSet = bookmarkList.toSet()

        val editor = preferences.edit()
        editor.putStringSet(KEY_BOOKMARKS, bookmarkSet)
        editor.apply()
    }

    private fun parseBibleVerseToBookmarkString(bookPage: BookPage, bibleVerse: BibleVerse): String {
        return "${bookPage.book}:${bookPage.page}:${bibleVerse.chapter}:${bibleVerse.verse}"
    }

    private fun parseBookmarkStringToBookPageTextPair(context: Context, bookmarkString: String): Pair<BookPage, String> {
        val books = context.resources.getStringArray(R.array.books)
        val components = bookmarkString.split(":")
        val bookPage = BookPage(
            book = components[0].toInt(),
            page = components[1].toInt()
        )
        val text = "${books[components[0].toInt().dec()]} ${components[2]}장 ${components[3]}절"

        return bookPage to text
    }
}