package com.graytsar.sensor.utils

import android.hardware.Sensor


const val ARG_SENSOR_TYPE = "ARG_SENSOR_TYPE"

const val keyPreferenceTheme = "preferenceTheme"
const val keyTheme = "theme"

object StaticValues {
    var isNightMode: Boolean = false

    val sensorTypeList: IntArray = intArrayOf(
        Sensor.TYPE_ACCELEROMETER,
        Sensor.TYPE_MAGNETIC_FIELD,
        Sensor.TYPE_GRAVITY,
        Sensor.TYPE_GYROSCOPE,
        Sensor.TYPE_LINEAR_ACCELERATION,
        Sensor.TYPE_AMBIENT_TEMPERATURE,
        Sensor.TYPE_LIGHT,
        Sensor.TYPE_PRESSURE,
        Sensor.TYPE_RELATIVE_HUMIDITY,
        Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR,
        Sensor.TYPE_PROXIMITY,
        Sensor.TYPE_STEP_COUNTER
    )
}