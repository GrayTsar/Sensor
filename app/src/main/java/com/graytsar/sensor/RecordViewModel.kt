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

class RecordViewModel : ViewModel() {
    @SuppressLint("StaticFieldLeak")
    var recordService: RecordService? = null
        set(value) {
            field = value
            if (value != null) collectRecordServiceState()
        }

    private val _recordServiceState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val recordServicesState = _recordServiceState.asStateFlow()

    private val recordServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as RecordService.LocalBinder
            recordService = binder.getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            _recordServiceState.tryEmit(false)
        }
    }

    private fun collectRecordServiceState() {
        viewModelScope.launch {
            recordService?.state?.collect {
                _recordServiceState.emit(it)
            }
        }
    }

    fun bindService(activity: AppCompatActivity) {
        Intent(activity, RecordService::class.java).also { intent ->
            activity.bindService(intent, recordServiceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    fun unbindService(activity: AppCompatActivity) {
        activity.unbindService(recordServiceConnection)
    }

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
        super.onCleared()
        recordService = null
    }
}