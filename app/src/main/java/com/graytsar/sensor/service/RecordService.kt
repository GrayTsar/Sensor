package com.graytsar.sensor.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.PendingIntentCompat
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.graytsar.sensor.R
import com.graytsar.sensor.model.UISensor
import com.graytsar.sensor.repository.entity.RecordEntity
import com.graytsar.sensor.repository.repository.RecordRepository
import com.graytsar.sensor.util.PermissionUtil
import com.graytsar.sensor.utils.Globals
import com.graytsar.sensor.utils.RECORD_CHANNEL_ID
import com.graytsar.sensor.utils.RECORD_NOTIFICATION_ID
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.LinkedTransferQueue
import javax.inject.Inject

@AndroidEntryPoint
class RecordService : Service() {
    @Inject
    lateinit var sensorManager: SensorManager

    @Inject
    lateinit var recordRepository: RecordRepository

    private val binder = LocalBinder()

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    private var model: UISensor? = null
    private var recordId: Long = -1

    private var recordedCount = 0
    private var isRecording = false
    private val events = LinkedTransferQueue<RecordEntity>()
    private val transfers = mutableListOf<RecordEntity>()

    private val _state: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val state = _state.asStateFlow()

    private val builder: NotificationCompat.Builder
        get() = NotificationCompat.Builder(applicationContext, RECORD_CHANNEL_ID).apply {
            setContentTitle(applicationContext.getString(model!!.title))
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
            events.add(
                RecordEntity(
                    id = 0,
                    recordId = recordId,
                    timestamp = event.timestamp,
                    x = event.values[0],
                    y = event.values[1],
                    z = event.values[2]
                )
            )
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val enableLog: Boolean = intent!!.getBooleanExtra(ARG_ENABLED, false)

        if (enableLog) {
            _state.tryEmit(true)
            startRecordingService(intent)
        } else {
            _state.tryEmit(false)
            isRecording = false
            stopRecordingService()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        _state.tryEmit(false)
        super.onDestroy()
        try {
            sensorManager.unregisterListener(sensorEventListener)
            cancelRecording()
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    private fun startRecordingService(intent: Intent) {
        recordId = intent.getLongExtra(ARG_RECORDING_ID, -1)
        val sensorType = intent.getIntExtra(ARG_SENSOR_TYPE, 1)
        val sensor = sensorManager.getDefaultSensor(sensorType)

        //when recording for another sensor started
        if (model != null) {
            isRecording = false
            sensorManager.unregisterListener(sensorEventListener)
            job.cancel()
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

        launchRecording()
    }

    private fun stopRecordingService() {
        sensorManager.unregisterListener(sensorEventListener)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun launchRecording() {
        isRecording = true
        scope.launch {
            while (isRecording) {
                val count = events.drainTo(transfers, 1000)
                if (count != 0) {
                    recordedCount += count
                    recordRepository.insert(transfers)
                    transfers.clear()
                    notify(recordedCount.toString())
                } else {
                    delay(1000)
                }
            }
        }.invokeOnCompletion {
            /* do nothing */
        }
    }

    private fun cancelRecording() {
        isRecording = false
        job.cancel()
        transfers.clear()
        events.clear()
    }

    @SuppressLint("MissingPermission")
    private fun notify(text: String) {
        if (PermissionUtil.isNotificationPermissionGranted(applicationContext)) {
            val notification = builder.setContentText(text).build()
            startForeground(RECORD_NOTIFICATION_ID, notification)
        }
    }

    inner class LocalBinder : Binder() {
        fun getService(): RecordService = this@RecordService
    }

    companion object {
        const val ARG_ENABLED = "ARG_ENABLED"
        const val ARG_SENSOR_TYPE = "ARG_SENSOR_TYPE"
        const val ARG_RECORDING_ID = "ARG_RECORDING_ID"
    }
}