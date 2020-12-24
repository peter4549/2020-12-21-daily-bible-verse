package com.duke.elliot.biblereadinghabits.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "bible_verse", primaryKeys = ["book","chapter","verse"])
@Parcelize
data class BibleVerse(
    val book: Int,
    val chapter: Int,
    val verse: Int,
    val word: String
): Parcelable