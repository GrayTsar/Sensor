package com.graytsar.sensor.ui.export

import android.content.ContentValues
import android.content.Context
import android.hardware.SensorManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.graytsar.sensor.repository.data.Record
import com.graytsar.sensor.repository.repository.RecordRepository
import com.graytsar.sensor.repository.repository.SensorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val sensorManager: SensorManager,
    val sensorRepository: SensorRepository,
    val recordRepository: RecordRepository
) : ViewModel() {

    private val pageDataFlow: MutableStateFlow<Flow<PagingData<Record>>> = MutableStateFlow(
        sensorRepository.getRecordsPaged()
    )
    val pager = pageDataFlow.flatMapLatest { it }.cachedIn(viewModelScope)

    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveFileToDownloads(
        context: Context,
        sensorType: Int,
        content: String,
        fileName: String
    ): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val resolver = context.contentResolver
        val uri =
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues) ?: return null
        resolver.openOutputStream(uri).use { output ->
            output?.write(getHeaderString(sensorType).toByteArray())
            output?.write(content.toByteArray())
        }
        return uri
    }

    fun saveFileToDownloads(content: String, sensorType: Int, fileName: String): Uri {
        val target = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        FileOutputStream(target).use { output ->
            output.write(getHeaderString(sensorType).toByteArray())
            output.write(content.toByteArray())
        }
        return Uri.fromFile(target)
    }

    private fun getHeaderString(sensorType: Int): String {
        val sensor = sensorManager.getDefaultSensor(sensorType)
        return "TIMESTAMP,X,Y,Z,NAME:${sensor.name},VENDOR:${sensor.vendor},VERSION:${sensor.version},POWER:${sensor.power}mA,MAXDELAY:${sensor.maxDelay},MINDELAY:${sensor.minDelay},MAXRANGE:${sensor.maximumRange}${System.lineSeparator()}"
    }
}