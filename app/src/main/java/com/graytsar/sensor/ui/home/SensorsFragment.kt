package com.graytsar.sensor.ui.home

import android.content.Context
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
import com.graytsar.sensor.R
import com.graytsar.sensor.SensorsActivity
import com.graytsar.sensor.databinding.FragmentSensorsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SensorsFragment : Fragment() {
    private val viewModel: ViewModelSensors by viewModels()

    private var _binding: FragmentSensorsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerHome: RecyclerView
    private lateinit var adapterSensor: AdapterSensor

    private lateinit var sensorManager: SensorManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSensorsBinding.inflate(inflater, container, false)

        val toolbar: Toolbar = binding.includeToolbarHome.toolbarHome
        (requireActivity() as SensorsActivity).setSupportActionBar(toolbar)

        val navController = NavHostFragment.findNavController(this)
        val drawerLayout: DrawerLayout =
            (requireActivity() as SensorsActivity).findViewById(R.id.drawer_layout)
        NavigationUI.setupActionBarWithNavController(
            requireActivity() as SensorsActivity,
            navController,
            drawerLayout
        )

        recyclerHome = binding.recyclerHome
        adapterSensor = AdapterSensor(requireActivity())
        recyclerHome.adapter = adapterSensor

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        //reset status bar color from details fragment color change
        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSensors()
    }

    override fun onStart() {
        super.onStart()
        registerSensors()
    }

    override fun onStop() {
        unregisterSensors()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initSensors() {
        adapterSensor.submitList(viewModel.sensors)
    }

    private fun registerSensors() {
        viewModel.sensorListeners.forEach {
            sensorManager.registerListener(
                it.value,
                sensorManager.getDefaultSensor(it.key),
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    private fun unregisterSensors() {
        viewModel.sensorListeners.forEach {
            sensorManager.unregisterListener(it.value)
        }
    }
}