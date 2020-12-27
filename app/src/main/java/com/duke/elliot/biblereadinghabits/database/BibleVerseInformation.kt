package com.duke.elliot.biblereadinghabits.database

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BibleVerseInformation(
    val book: Int,
    val chapter: Int,
    val verse: Int
): Parcelable