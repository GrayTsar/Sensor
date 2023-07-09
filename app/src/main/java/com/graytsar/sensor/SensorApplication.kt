package com.graytsar.sensor

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.graytsar.sensor.utils.Globals
import com.graytsar.sensor.utils.RECORD_CHANNEL_ID
import com.graytsar.sensor.utils.keyPreferenceTheme
import com.graytsar.sensor.utils.keyTheme
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
open class SensorApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> Globals.isNightMode = true

            Configuration.UI_MODE_NIGHT_NO -> {
                val sharedPref = this.getSharedPreferences(keyPreferenceTheme, Context.MODE_PRIVATE)
                if (sharedPref.getBoolean(keyTheme, false)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    Globals.isNightMode = true
                } else {
                    Globals.isNightMode = false
                }
            }

            Configuration.UI_MODE_NIGHT_UNDEFINED -> Globals.isNightMode = false
        }

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val loggingChannel = NotificationChannel(
            RECORD_CHANNEL_ID,
            getString(R.string.record_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = getString(R.string.record_channel_description)
            setSound(null, null)
        }

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(loggingChannel)
    }
}