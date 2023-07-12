package com.graytsar.sensor

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.graytsar.sensor.service.RecordService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel to get the state of the [RecordService]
 */
class RecordViewModel : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    //auto start state collection when set.
    var recordService: RecordService? = null
        set(value) {
            field = value
            if (value != null) collectRecordServiceState()
        }

    /**
     * State of the [RecordService]
     */
    private val _recordServiceState: MutableStateFlow<Boolean> = MutableStateFlow(false)

    /**
     * Read only state of the [RecordService]
     */
    val recordServicesState = _recordServiceState.asStateFlow()

    /**
     * Connection to the [RecordService]
     */
    private val recordServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RecordService.LocalBinder
            recordService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            _recordServiceState.tryEmit(false)
        }
    }

    /**
     * Collects the state of the [RecordService].
     */
    private fun collectRecordServiceState() {
        viewModelScope.launch {
            recordService?.state?.collect {
                _recordServiceState.emit(it)
            }
        }
    }

    /**
     * Binds the [RecordService] to the activity.
     */
    fun bindService(activity: AppCompatActivity) {
        Intent(activity, RecordService::class.java).also { intent ->
            activity.bindService(intent, recordServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    /**
     * Unbinds the [RecordService] from the activity.
     */
    fun unbindService(activity: AppCompatActivity) {
        activity.unbindService(recordServiceConnection)
    }

    /**
     * Unbinds and stops the [RecordService] from the activity.
     */
    fun unbindAndStopService(activity: AppCompatActivity) {
        activity.unbindService(recordServiceConnection)
        val intent = Intent(activity, RecordService::class.java).apply {
            putExtra(RecordService.ARG_ENABLED, false)
        }
        activity.stopService(intent)
        _recordServiceState.tryEmit(false)
        recordService = null
    }

    override fun onCleared() {
        recordService = null
        super.onCleared()
    }
}