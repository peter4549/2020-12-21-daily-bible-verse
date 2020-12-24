package com.duke.elliot.biblereadinghabits.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PopularBibleVerseDao {
    @Query("SELECT * FROM popular_bible_verse ORDER BY id ASC")
    fun getAll(): LiveData<MutableList<PopularBibleVerse>>

    @Query("SELECT * FROM popular_bible_verse ORDER BY id ASC")
    fun getAllValues(): List<PopularBibleVerse>

    @Insert
    fun insert(popularBibleVerse: PopularBibleVerse)

    @Update
    fun update(popularBibleVerse: PopularBibleVerse)

    @Delete
    fun delete(popularBibleVerse: PopularBibleVerse)

    @Query("SELECT * FROM popular_bible_verse WHERE id LIKE :id")
    fun get(id: Long): List<PopularBibleVerse>
}