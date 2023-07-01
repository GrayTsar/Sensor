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
import com.graytsar.sensor.model.UISensor
import com.graytsar.sensor.utils.Globals
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModelHome: ViewModelHome by viewModels<ViewModelHome>()

    private lateinit var recyclerHome: RecyclerView
    private lateinit var adapterSensor: AdapterSensor

    private lateinit var sensorManager: SensorManager
    private var arListSensorEventListener: ArrayList<SensorEventListener> = ArrayList()
    private var sensors: ArrayList<UISensor> = ArrayList<UISensor>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        //initAdapter()

        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.primary_dark)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        //registerAllSensor()
    }

    override fun onStop() {
        //unregisterAllListener()
        super.onStop()
    }

    override fun onDestroyView() {
        //sensors.clear()
        super.onDestroyView()
    }

    private fun registerAllSensor() {
        arListSensorEventListener.clear()

        sensors.forEach { model ->
            var listener: SensorEventListener? = null

            if (model.valuesCount == 1) {
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
                SensorManager.SENSOR_DELAY_UI
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
        sensors.clear()
        Globals.sensors.filterTo(sensors) {
            sensorManager.getDefaultSensor(it.sensorType) != null
        }
        adapterSensor.submitList(sensors)
    }
}