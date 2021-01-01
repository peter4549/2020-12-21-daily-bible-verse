package com.duke.elliot.biblereadinghabits.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.util.getVersionName
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}