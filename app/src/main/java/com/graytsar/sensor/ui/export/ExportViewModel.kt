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
import com.graytsar.sensor.repository.entity.RecordEntity
import com.graytsar.sensor.repository.repository.RecordRepository
import com.graytsar.sensor.repository.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import java.io.File
import javax.inject.Inject

/**
 * ViewModel for the [ExportFragment].
 */
@HiltViewModel
class ExportViewModel @Inject constructor(
    private val sensorManager: SensorManager,
    val sessionRepository: SessionRepository,
    private val recordRepository: RecordRepository
) : ViewModel() {

    /**
     * Flow of [PagingData] that represents the list of records.
     */
    private val pageDataFlow: MutableStateFlow<Flow<PagingData<Record>>> = MutableStateFlow(
        sessionRepository.getRecordsPaged()
    )

    /**
     * Ready only flow of recording sessions.
     */
    val pager = pageDataFlow.flatMapLatest { it }.cachedIn(viewModelScope)

    /**
     * Writes the sensor events to a file in the downloads folder.
     *
     * @param context the context of the application to get file uri.
     * @param sensorType the type of sensor.
     * @param recordingId the id of the recording session.
     * @param fileName the name of the file.
     *
     * @return the [Uri] of the file.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    suspend fun saveFileToDownloads(
        context: Context,
        sensorType: Int,
        recordingId: Long,
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
            val recordings = mutableListOf<RecordEntity>()
            val limit = 10000
            var offset = 0

            output?.write(getHeaderString(sensorType).toByteArray())
            while (true) {
                //write the sensor events paginated to the file. Since there could be a large amount of data.
                val rows = recordRepository.getByRecodingLimit(recordingId, limit, offset)
                if (rows.isEmpty()) {
                    break
                }
                recordings.clear()
                recordings.addAll(rows)
                offset += rows.count()
                val content = recordings.joinToString(System.lineSeparator()) {
                    "${it.timestamp},${it.x},${it.y},${it.z}"
                }
                output?.write(content.toByteArray())
            }
        }
        return uri
    }

    /**
     * Writes the sensor events to a file in the downloads folder.
     * @param sensorType the type of sensor.
     * @param recordingId the id of the recording session.
     * @param fileName the name of the file.
     *
     * @return the [Uri] of the file
     */
    suspend fun saveFileToDownloads(sensorType: Int, recordingId: Long, fileName: String): Uri {
        val target = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            fileName
        )
        target.outputStream().use { output ->
            val recordings = mutableListOf<RecordEntity>()
            val limit = 10000
            var offset = 0
            output.write(getHeaderString(sensorType).toByteArray())

            output.write(getHeaderString(sensorType).toByteArray())
            while (true) {
                //write the sensor events paginated to the file. Since there could be a large amount of data.
                val rows = recordRepository.getByRecodingLimit(recordingId, limit, offset)
                if (rows.isEmpty()) {
                    break
                }
                recordings.clear()
                recordings.addAll(rows)
                offset += rows.count()
                val content = recordings.joinToString(System.lineSeparator()) {
                    "${it.timestamp},${it.x},${it.y},${it.z}"
                }
                output.write(content.toByteArray())
            }
        }
        return Uri.fromFile(target)
    }

    /**
     * Create the header for the csv file.
     *
     * @param sensorType the type of sensor.
     *
     * @return the header string.
     */
    private fun getHeaderString(sensorType: Int): String {
        val sensor = sensorManager.getDefaultSensor(sensorType)
        return "TIMESTAMP,X,Y,Z,NAME:${sensor?.name.orEmpty()},VENDOR:${sensor?.vendor.orEmpty()}," +
                "VERSION:${sensor?.version},POWER:${sensor?.power}mA,MAXDELAY:${sensor?.maxDelay}," +
                "MINDELAY:${sensor?.minDelay},MAXRANGE:${sensor?.maximumRange}${System.lineSeparator()}"
    }
}