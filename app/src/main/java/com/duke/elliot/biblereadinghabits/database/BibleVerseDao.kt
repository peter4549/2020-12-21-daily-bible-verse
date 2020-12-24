package com.duke.elliot.biblereadinghabits.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.duke.elliot.biblereadinghabits.database.BibleVerse

@Dao
interface BibleVerseDao {
    @Query("SELECT * FROM bible_verse")
    fun getAll(): LiveData<MutableList<BibleVerse>>

    @Insert
    fun insert(bibleVerse: BibleVerse)

    @Update
    fun update(bibleVerse: BibleVerse)

    @Delete
    fun delete(bibleVerse: BibleVerse)

    @Query("SELECT * FROM bible_verse WHERE book = :book ORDER BY chapter ASC, verse ASC")
    fun getBook(book: Int): LiveData<List<BibleVerse>>

    @Query("SELECT * FROM bible_verse WHERE book = :book AND chapter = :chapter")
    fun getBookChapter(book: Int, chapter: Int): List<BibleVerse>
}