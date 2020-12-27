package com.duke.elliot.biblereadinghabits.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.duke.elliot.biblereadinghabits.database.BibleVerse

@Dao
interface BibleVerseDao {
    @Query("SELECT * FROM bible_verse")
    fun getAll(): LiveData<MutableList<BibleVerse>>

    @Query("SELECT * FROM bible_verse ORDER BY book ASC, chapter ASC, verse ASC")
    fun getAllValue(): List<BibleVerse>

    @Insert
    fun insert(bibleVerse: BibleVerse)

    @Update
    fun update(bibleVerse: BibleVerse)

    @Delete
    fun delete(bibleVerse: BibleVerse)

    @Query("SELECT * FROM bible_verse WHERE book = :book ORDER BY chapter ASC, verse ASC")
    fun getBook(book: Int): LiveData<List<BibleVerse>>

    @Query("SELECT * FROM bible_verse WHERE book = :book ORDER BY chapter ASC, verse ASC")
    fun getBookValue(book: Int): List<BibleVerse>

    @Query("SELECT * FROM bible_verse WHERE book = :book AND chapter = :chapter")
    fun getBookChapter(book: Int, chapter: Int): List<BibleVerse>

    @Query("SELECT * FROM bible_verse WHERE book = :book AND chapter = :chapter AND verse = :verse LIMIT 1")
    fun getBibleVerse(book: Int, chapter: Int, verse: Int): BibleVerse
}