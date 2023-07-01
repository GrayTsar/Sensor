package com.graytsar.sensor.utils

import android.hardware.Sensor
import com.graytsar.sensor.R
import com.graytsar.sensor.model.UISensor

const val ARG_SENSOR_TYPE = "ARG_SENSOR_TYPE"
const val keyPreferenceTheme = "preferenceTheme"
const val keyTheme = "theme"

object Globals {
    var isNightMode: Boolean = false

    val sensors = listOf(
        UISensor(
            sensorType = Sensor.TYPE_ACCELEROMETER,
            valuesCount = 3,
            title = R.string.sensorAccelerometer,
            unit = R.string.unitAcceleration
        ),
        UISensor(
            sensorType = Sensor.TYPE_MAGNETIC_FIELD,
            valuesCount = 3,
            title = R.string.sensorMagneticField,
            unit = R.string.unitMagneticField
        ),
        UISensor(
            sensorType = Sensor.TYPE_GRAVITY,
            valuesCount = 3,
            title = R.string.sensorGravity,
            unit = R.string.unitAcceleration
        ),
        UISensor(
            sensorType = Sensor.TYPE_GYROSCOPE,
            valuesCount = 3,
            title = R.string.sensorGyroscope,
            unit = R.string.unitAngularVelocity
        ),
        UISensor(
            sensorType = Sensor.TYPE_LINEAR_ACCELERATION,
            valuesCount = 3,
            title = R.string.sensorLinearAcceleration,
            unit = R.string.unitAcceleration
        ),
        UISensor(
            sensorType = Sensor.TYPE_AMBIENT_TEMPERATURE,
            valuesCount = 1,
            title = R.string.sensorAmbientTemperature,
            unit = R.string.unitTemperature
        ),
        UISensor(
            sensorType = Sensor.TYPE_LIGHT,
            valuesCount = 1,
            title = R.string.sensorLight,
            unit = R.string.unitIlluminance
        ),
        UISensor(
            sensorType = Sensor.TYPE_PRESSURE,
            valuesCount = 1,
            title = R.string.sensorPressure,
            unit = R.string.unitPressure
        ),
        UISensor(
            sensorType = Sensor.TYPE_RELATIVE_HUMIDITY,
            valuesCount = 1,
            title = R.string.sensorRelativeHumidity,
            unit = R.string.unitPercent
        ),
        UISensor(
            sensorType = Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR,
            valuesCount = 3,
            title = R.string.sensorGeomagneticRotationVector,
            unit = R.string.unitNone
        ),
        UISensor(
            sensorType = Sensor.TYPE_PROXIMITY,
            valuesCount = 1,
            title = R.string.sensorProximity,
            unit = R.string.unitProximity
        ),
        UISensor(
            sensorType = Sensor.TYPE_STEP_COUNTER,
            valuesCount = 1,
            title = R.string.sensorStepCounter,
            unit = R.string.unitStep
        )
    )
}