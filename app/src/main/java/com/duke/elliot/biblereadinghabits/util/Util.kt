package com.duke.elliot.biblereadinghabits.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.duke.elliot.biblereadinghabits.R

fun shareApplication(context: Context) {
    val intent = Intent(Intent.ACTION_SEND)
    val text = "https://play.google.com/store/apps/details?id=${context.packageName}"

    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, text)

    val chooser = Intent.createChooser(intent, context.getString(R.string.share_the_app))
    context.startActivity(chooser)
}

fun getVersionName(context: Context): String {
    return try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
        return "Version info not found."
    }
}

fun goToPlayStore(context: Context) {
    try {
        context.startActivity(
            Intent (
                Intent.ACTION_VIEW,
                Uri.parse("market://details?id=${context.packageName}"))
        )
    } catch (e: ActivityNotFoundException) {
        context.startActivity(
            Intent (
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")
            )
        )
    }
}