package com.duke.elliot.biblereadinghabits.database

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "favorite_bible_verse", primaryKeys = ["book", "chapter", "verse"])
@Parcelize
data class FavoriteBibleVerse(
    val book: Int,
    val chapter: Int,
    val verse: Int,
    val word: String,
    @ColumnInfo(name = "added_time") val addedTime: Long
): Parcelable