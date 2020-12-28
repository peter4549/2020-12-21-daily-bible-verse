package com.duke.elliot.biblereadinghabits.bible_reading

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BookPage(
    var book: Int,
    var page: Int
): Parcelable