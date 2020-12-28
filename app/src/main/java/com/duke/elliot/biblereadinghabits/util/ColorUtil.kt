package com.duke.elliot.biblereadinghabits.util

import android.graphics.Color
import androidx.annotation.ColorInt

@ColorInt
fun Int.setAlphaValue(percentage: Int): Int {
    val red: Int = Color.red(this)
    val blue: Int = Color.blue(this)
    val green: Int = Color.green(this)
    val alpha = percentage * 255 / 100
    return Color.argb(alpha, red, blue, green)
}