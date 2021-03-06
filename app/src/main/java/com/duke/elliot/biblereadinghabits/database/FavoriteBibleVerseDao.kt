package com.duke.elliot.biblereadinghabits.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.duke.elliot.biblereadinghabits.database.BibleVerse

@Dao
interface FavoriteBibleVerseDao {
    @Query("SELECT * FROM favorite_bible_verse")
    fun getAll(): LiveData<MutableList<FavoriteBibleVerse>>

    @Query("SELECT * FROM favorite_bible_verse ORDER BY added_time ASC")
    fun getAllValue(): List<FavoriteBibleVerse>

    @Insert
    fun insert(favoriteBibleVerse: FavoriteBibleVerse)

    @Update
    fun update(favoriteBibleVerse: FavoriteBibleVerse)

    @Delete
    fun delete(favoriteBibleVerse: FavoriteBibleVerse)
}