package com.graytsar.sensor

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.PendingIntentCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.graytsar.sensor.model.UISensor
import com.graytsar.sensor.util.PermissionUtil
import com.graytsar.sensor.utils.Globals
import com.graytsar.sensor.utils.RECORD_CHANNEL_ID
import com.graytsar.sensor.utils.RECORD_NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecordService : Service() {
    @Inject
    lateinit var sensorManager: SensorManager
    private var model: UISensor? = null

    private val builder: NotificationCompat.Builder
        get() = NotificationCompat.Builder(applicationContext, RECORD_CHANNEL_ID).apply {
            setContentTitle(applicationContext.getString(R.string.record_channel_name))
            setSmallIcon(R.drawable.ic_app)
            setOngoing(true)
            addAction(
                R.drawable.ic_close_24,
                getString(android.R.string.cancel),
                PendingIntentCompat.getService(
                    /* context = */ applicationContext,
                    /* requestCode = */ 0,
                    /* intent = */ Intent(applicationContext, RecordService::class.java).apply {
                        putExtra(ARG_ENABLED, false)
                    },
                    /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT,
                    /* isMutable = */ false
                )
            )
            priority = NotificationCompat.PRIORITY_DEFAULT
        }

    private var sensorEventListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            if (event == null) return
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val enableLog: Boolean = intent!!.getBooleanExtra(ARG_ENABLED, false)

        if (enableLog) {
            startRecording(intent)
        } else {
            stopRecording()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            sensorManager.unregisterListener(sensorEventListener)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun startRecording(intent: Intent) {
        val sensorType = intent.getIntExtra(ARG_SENSOR_TYPE, 1)
        val sensor = sensorManager.getDefaultSensor(sensorType)

        //when recording for another sensor started
        if (model != null) {
            sensorManager.unregisterListener(sensorEventListener)
        }

        model = Globals.sensors.find { it.sensorType == sensorType }!!

        sensorManager.registerListener(
            sensorEventListener,
            sensor,
            SensorManager.SENSOR_DELAY_FASTEST
        )

        startForeground(
            RECORD_NOTIFICATION_ID,
            builder.setContentText(getString(model!!.title)).build()
        )
    }

    private fun stopRecording() {
        sensorManager.unregisterListener(sensorEventListener)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    @SuppressLint("MissingPermission")
    private fun notify(text: String) {
        if (PermissionUtil.isNotificationPermissionGranted(applicationContext)) {
            val notification = builder.setContentText(text).build()
            startForeground(RECORD_NOTIFICATION_ID, notification)
        }
    }

    companion object {
        const val ARG_ENABLED = "ENABLED"
        const val ARG_SENSOR_TYPE = "SENSOR_TYPE"
    }
}