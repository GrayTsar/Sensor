package com.graytsar.sensor.ui.export

import android.hardware.SensorManager
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
import javax.inject.Inject

@HiltViewModel
class ExportViewModel @Inject constructor(
    private val sensorManager: SensorManager,
    val sensorRepository: SensorRepository,
    private val recordRepository: RecordRepository
) : ViewModel() {

    //val csvHeader =
    //    "TIMESTAMP,X,Y,Z,NAME:${sensor.name},VENDOR:${sensor.vendor},VERSION:${sensor.version},POWER:${sensor.power}mA,MAXDELAY:${sensor.maxDelay},MINDELAY:${sensor.minDelay},MAXRANGE:${sensor.maximumRange}"

    private val pageDataFlow: MutableStateFlow<Flow<PagingData<Record>>> = MutableStateFlow(
        sensorRepository.getRecordsPaged()
    )
    val pager = pageDataFlow.flatMapLatest { it }.cachedIn(viewModelScope)


}