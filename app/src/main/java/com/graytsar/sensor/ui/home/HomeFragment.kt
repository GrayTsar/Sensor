package com.graytsar.sensor.ui.home

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.RecyclerView
import com.graytsar.sensor.MainActivity
import com.graytsar.sensor.R
import com.graytsar.sensor.databinding.FragmentHomeBinding
import com.graytsar.sensor.model.ModelSensor
import com.graytsar.sensor.utils.StaticValues

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModelHome: ViewModelHome by viewModels<ViewModelHome>()

    private lateinit var recyclerHome: RecyclerView
    private lateinit var adapterSensor: AdapterSensor

    private lateinit var sensorManager: SensorManager
    private var arListSensorEventListener: ArrayList<SensorEventListener> = ArrayList()
    private var listSensor: ArrayList<ModelSensor> = ArrayList<ModelSensor>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val toolbar: Toolbar = binding.includeToolbarHome.toolbarHome
        (requireActivity() as MainActivity).setSupportActionBar(toolbar)

        val navController = NavHostFragment.findNavController(this)
        val drawerLayout: DrawerLayout = (context as MainActivity).findViewById(R.id.drawer_layout)
        NavigationUI.setupActionBarWithNavController(
            this.context as MainActivity,
            navController,
            drawerLayout
        )

        recyclerHome = binding.recyclerHome
        adapterSensor = AdapterSensor(requireActivity())
        recyclerHome.adapter = adapterSensor

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        initAdapter()

        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.primary_dark)

        return binding.root
    }

    override fun onStart() {
        super.onStart()

        registerAllSensor()
    }

    override fun onStop() {
        super.onStop()

        unregisterAllListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        listSensor.clear()
    }

    private fun registerAllSensor() {
        arListSensorEventListener.clear()

        listSensor.forEach { model ->
            var listener: SensorEventListener? = null

            if (model.sensorValuesCount == 1) {
                listener = object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent?) {
                        event?.let { event ->
                            model.xValue.postValue("${event.values[0]} ${model.unit}")
                        }
                    }

                    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                    }
                }
            } else {
                listener = object : SensorEventListener {
                    override fun onSensorChanged(event: SensorEvent?) {
                        event?.let {
                            model.xValue.value = "${event.values[0]} ${model.unit}"
                            model.yValue.value = "${event.values[1]} ${model.unit}"
                            model.zValue.value = "${event.values[2]} ${model.unit}"
                        }
                    }

                    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
                    }
                }
            }

            arListSensorEventListener.add(listener)
            sensorManager.registerListener(
                listener,
                sensorManager.getDefaultSensor(model.sensorType),
                SensorManager.SENSOR_DELAY_FASTEST
            )
        }
    }

    private fun unregisterAllListener() {
        for (sensorEventListener in arListSensorEventListener) {
            sensorManager.unregisterListener(sensorEventListener)
        }
        arListSensorEventListener.clear()
    }

    private fun initAdapter() {
        StaticValues.sensorTypeList.forEach {
            val mSensor = sensorManager.getDefaultSensor(it)

            if (mSensor != null) {
                when (mSensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        val model = ModelSensor(
                            Sensor.TYPE_ACCELEROMETER,
                            getString(R.string.sensorAccelerometer),
                            3,
                            getString(R.string.unitAcceleration)
                        )
                        listSensor.add(model)
                    }

                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        val model = ModelSensor(
                            Sensor.TYPE_MAGNETIC_FIELD,
                            getString(R.string.sensorMagneticField),
                            3,
                            getString(R.string.unitMagneticField)
                        )
                        listSensor.add(model)
                    }

                    Sensor.TYPE_GRAVITY -> {
                        val model = ModelSensor(
                            Sensor.TYPE_GRAVITY,
                            getString(R.string.sensorGravity),
                            3,
                            getString(R.string.unitAcceleration)
                        )
                        listSensor.add(model)
                    }

                    Sensor.TYPE_GYROSCOPE -> {
                        val model = ModelSensor(
                            Sensor.TYPE_GYROSCOPE,
                            getString(R.string.sensorGyroscope),
                            3,
                            getString(R.string.unitRadiantSecond)
                        )
                        listSensor.add(model)
                    }

                    Sensor.TYPE_LINEAR_ACCELERATION -> {
                        val model = ModelSensor(
                            Sensor.TYPE_LINEAR_ACCELERATION,
                            getString(R.string.sensorLinearAcceleration),
                            3,
                            getString(R.string.unitAcceleration)
                        )
                        listSensor.add(model)
                    }

                    Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                        val model = ModelSensor(
                            Sensor.TYPE_AMBIENT_TEMPERATURE,
                            getString(R.string.sensorAmbientTemperature),
                            1,
                            getString(R.string.unitTemperature)
                        )
                        listSensor.add(model)
                    }

                    Sensor.TYPE_LIGHT -> {
                        val model = ModelSensor(
                            Sensor.TYPE_LIGHT,
                            getString(R.string.sensorLight),
                            1,
                            getString(R.string.unitLight)
                        )
                        listSensor.add(model)
                    }

                    Sensor.TYPE_PRESSURE -> {
                        val model = ModelSensor(
                            Sensor.TYPE_PRESSURE,
                            getString(R.string.sensorPressure),
                            1,
                            getString(R.string.unitPressure)
                        )
                        listSensor.add(model)
                    }

                    Sensor.TYPE_RELATIVE_HUMIDITY -> {
                        val model = ModelSensor(
                            Sensor.TYPE_RELATIVE_HUMIDITY,
                            getString(R.string.sensorRelativeHumidity),
                            1,
                            getString(R.string.unitPercent)
                        )
                        listSensor.add(model)
                    }

                    Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR -> {
                        val model = ModelSensor(
                            Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR,
                            getString(R.string.sensorGeomagneticRotationVector),
                            3,
                            ""
                        )
                        listSensor.add(model)
                    }

                    Sensor.TYPE_PROXIMITY -> {
                        val model = ModelSensor(
                            Sensor.TYPE_PROXIMITY,
                            getString(R.string.sensorProximity),
                            1,
                            getString(R.string.unitProximity)
                        )
                        listSensor.add(model)
                    }

                    Sensor.TYPE_STEP_COUNTER -> {
                        val model = ModelSensor(
                            Sensor.TYPE_STEP_COUNTER,
                            getString(R.string.sensorStepCounter),
                            1,
                            getString(R.string.unitSteps)
                        )
                        listSensor.add(model)
                    }
                }
            }
        }

        adapterSensor.submitList(listSensor)
    }
}