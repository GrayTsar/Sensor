package com.graytsar.sensor.ui.sensors

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
import com.graytsar.sensor.utils.Globals
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SensorsFragment : Fragment() {
    private val viewModel: SensorsViewModel by viewModels()

    private lateinit var recyclerHome: RecyclerView
    private lateinit var adapterSensor: AdapterSensor

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSensorsBinding.inflate(inflater, container, false)

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
        //FIXME: this is a hack to fix listener updating wrong view holder
        recyclerHome.recycledViewPool.setMaxRecycledViews(-1, 0)
        recyclerHome.setItemViewCacheSize(Globals.sensors.size)

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
        viewModel.registerListeners()
    }

    override fun onPause() {
        viewModel.unregisterListeners()
        super.onPause()
    }

    private fun initSensors() {
        adapterSensor.submitList(viewModel.sensors)
    }
}