package com.duke.elliot.biblereadinghabits.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.duke.elliot.biblereadinghabits.R
import com.duke.elliot.biblereadinghabits.main.MainActivity
import com.duke.elliot.biblereadinghabits.main.MainApplication
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import timber.log.Timber

class SplashActivity: AppCompatActivity() {

    private lateinit var firebaseRemoteConfig: FirebaseRemoteConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.statusBarColor = MainApplication.primaryThemeColor
        startApplicationWithFirebaseRemoteConfig()
    }

    private fun startApplicationWithFirebaseRemoteConfig() {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0)
            .build()
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config)
        firebaseRemoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    /**
                     * Keys:
                     * show_notification: Boolean
                     * show_version_update_request: Boolean
                     * version_name: String
                     * notification_title: String
                     * notification_message: String
                     */
                    Timber.d("Remote config activate successful.")

                    val showNotification =
                        firebaseRemoteConfig.getBoolean("show_notification")
                    val showVersionUpdateRequest =
                        firebaseRemoteConfig.getString("show_version_update_request")
                    val versionName =
                        firebaseRemoteConfig.getString("version_name")
                    val notificationTitle =
                        firebaseRemoteConfig.getString("notification_title")
                    val notificationMessage =
                        firebaseRemoteConfig.getString("notification_message")

                    latestVersionName = versionName
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Timber.e(task.exception)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
    }

    companion object {
        var latestVersionName = ""
    }
}