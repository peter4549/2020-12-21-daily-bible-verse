package com.duke.elliot.biblereadinghabits.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "popular_bible_verse")
class PopularBibleVerse(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val book: Int,
    val chapter: Int,
    val verse: Int
)