package com.graytsar.sensor.ui.detail

import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.graytsar.sensor.R
import com.graytsar.sensor.RecordViewModel
import com.graytsar.sensor.databinding.FragmentDetailBinding
import com.graytsar.sensor.service.RecordService
import com.graytsar.sensor.util.PermissionUtil
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * A fragment representing a single sensor in detail.
 * It shows detailed information about the sensor and a chart with past sensor events.
 * User can start a recording session for this sensor through a foreground service.
 */
@AndroidEntryPoint
class DetailFragment : Fragment() {
    private val recordViewModel: RecordViewModel by activityViewModels()
    private val viewModel: DetailViewModel by viewModels()

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var fab: FloatingActionButton
    private lateinit var name: TextView
    private lateinit var vendor: TextView
    private lateinit var version: TextView
    private lateinit var power: TextView
    private lateinit var maxDelay: TextView
    private lateinit var minDelay: TextView
    private lateinit var maxRange: TextView
    private lateinit var info: TextView
    private lateinit var xText: TextView
    private lateinit var yText: TextView
    private lateinit var zText: TextView

    private lateinit var chart: AAChartView

    private var looperJob: Job? = null
    private var isDrawn = false

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) startRecordingService()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

        val toolbar = binding.detail.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        val navController = NavHostFragment.findNavController(this)
        NavigationUI.setupActionBarWithNavController(
            requireActivity() as AppCompatActivity,
            navController
        )

        fab = binding.detail.fab
        name = binding.detail.name
        vendor = binding.detail.vendor
        version = binding.detail.version
        power = binding.detail.power
        maxDelay = binding.detail.maxDelay
        minDelay = binding.detail.minDelay
        maxRange = binding.detail.maxRange
        info = binding.detail.textInformation
        chart = binding.detail.chart
        chart.isClearBackgroundColor = true

        xText = binding.detail.xValDetail
        yText = binding.detail.yValDetail
        zText = binding.detail.zValDetail

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name.text = getString(R.string.labelName, viewModel.sensor.name)
        vendor.text = getString(R.string.labelVendor, viewModel.sensor.vendor)
        version.text = getString(R.string.labelVersion, viewModel.sensor.version.toString())
        power.text = getString(R.string.labelPower, viewModel.sensor.power.toString())
        maxDelay.text = getString(R.string.labelMaxDelay, viewModel.sensor.maxDelay.toString())
        minDelay.text = getString(R.string.labelMinDelay, viewModel.sensor.minDelay.toString())
        maxRange.text = getString(
            R.string.labelMaxRange,
            getString(viewModel.itemSensor.unit, viewModel.sensor.maximumRange)
        )
        info.text = getString(viewModel.itemSensor.info)

        if (viewModel.itemSensor.axes == 3) {
            yText.visibility = View.VISIBLE
            zText.visibility = View.VISIBLE
        }

        viewModel.itemSensor.let {
            val color = ContextCompat.getColor(requireContext(), it.color)
            val layout = binding.detail.toolbarLayout
            val icon = binding.detail.appBarImage

            layout.title = getString(it.name)
            layout.setBackgroundColor(color)
            layout.setContentScrimColor(color)
            activity?.window?.statusBarColor = color
            icon.setImageDrawable(ContextCompat.getDrawable(requireContext(), it.icon))
        }

        if (viewModel.isRecording) {
            fab.setImageResource(R.drawable.ic_baseline_pause_24)
        } else {
            fab.setImageResource(R.drawable.ic_baseline_play_arrow_24)
        }

        fab.setOnClickListener { onFabClicked() }

        viewLifecycleOwner.lifecycleScope.launch {
            recordViewModel.recordServicesState.collectLatest {
                viewModel.isRecording = it
                if (it) {
                    fab.setImageResource(R.drawable.ic_baseline_pause_24)
                } else {
                    fab.setImageResource(R.drawable.ic_baseline_play_arrow_24)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        viewModel.sensorManager.registerListener(
            viewModel.sensorEventListener,
            viewModel.sensor,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.itemSensor.axes == 1) {
            loopSingle()
        } else {
            loopMulti()
        }
    }

    override fun onPause() {
        looperJob?.cancel()
        super.onPause()
    }

    override fun onStop() {
        viewModel.sensorManager.unregisterListener(viewModel.sensorEventListener)
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loopSingle() {
        looperJob = viewLifecycleOwner.lifecycleScope.launch {
            while (true) {
                updateSingleChart()
                delay(16)
            }
        }
    }

    private fun loopMulti() {
        looperJob = viewLifecycleOwner.lifecycleScope.launch {
            while (true) {
                updateMultiChart()
                delay(16)
            }
        }
    }

    /**
     * Request notification permission to start [RecordService] in the foreground.
     * If permission is granted, [startRecordingService] is called.
     * User can start and stop the [RecordService].
     */
    private fun onFabClicked() {
        if (!PermissionUtil.isNotificationPermissionGranted(requireContext())) {
            requestNotificationPermission()
            return
        }

        if (viewModel.isRecording) {
            stopRecording()
        } else {
            startRecordingService()
        }
    }

    /**
     * Starts the [RecordService] in the foreground.
     */
    private fun startRecordingService() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val id = viewModel.insertSensor()
            val intent = Intent(context, RecordService::class.java).apply {
                putExtra(RecordService.ARG_ENABLED, true)
                putExtra(RecordService.ARG_SENSOR_TYPE, viewModel.sensorType)
                putExtra(RecordService.ARG_RECORDING_ID, id)
            }
            ContextCompat.startForegroundService(requireContext(), intent)
            recordViewModel.bindService(requireActivity() as AppCompatActivity)
        }
    }

    /**
     * Stops the [RecordService].
     */
    private fun stopRecording() {
        recordViewModel.unbindAndStopService(requireActivity() as AppCompatActivity)
    }

    /**
     * Updates the chart with the latest values for a single axis sensor.
     */
    private fun updateSingleChart() {
        xText.text = getString(
            viewModel.itemSensor.unit,
            viewModel.xValues.lastOrNull() ?: 0.0
        )

        if (isDrawn) {
            val elements = arrayOf(
                AASeriesElement()
                    .name("X")
                    .data(viewModel.xValues.toTypedArray())
            )
            chart.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(elements, false)
        } else {
            val chartModel = AAChartModel(
                chartType = AAChartType.Line,
                animationDuration = 0,
                animationType = null,
                touchEventEnabled = false,
                tooltipEnabled = false,
                dataLabelsEnabled = false,
                xAxisLabelsEnabled = false,
                yAxisTitle = "",
                yAxisGridLineWidth = 0f,
                axesTextColor = "#78909C"
            ).apply {
                series(
                    arrayOf(
                        mapOf(
                            "name" to "X",
                            "data" to viewModel.xValues.toTypedArray()
                        )
                    )
                )
            }
            chart.aa_drawChartWithChartModel(chartModel)
        }
    }

    /**
     * Updates the chart with the latest values for a multi axis sensor.
     */
    private fun updateMultiChart() {
        xText.text = getString(
            viewModel.itemSensor.unit,
            viewModel.xValues.lastOrNull() ?: 0.0
        )
        yText.text = getString(
            viewModel.itemSensor.unit,
            viewModel.yValues.lastOrNull() ?: 0.0
        )
        zText.text = getString(
            viewModel.itemSensor.unit,
            viewModel.zValues.lastOrNull() ?: 0.0
        )

        if (isDrawn) {
            val elements = arrayOf(
                AASeriesElement()
                    .name("X")
                    .data(viewModel.xValues.toTypedArray()),
                AASeriesElement()
                    .name("Y")
                    .data(viewModel.yValues.toTypedArray()),
                AASeriesElement()
                    .name("Z")
                    .data(viewModel.zValues.toTypedArray())
            )
            chart.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(elements, false)
        } else {
            val chartModel = AAChartModel(
                chartType = AAChartType.Line,
                animationDuration = 0,
                animationType = null,
                touchEventEnabled = false,
                tooltipEnabled = false,
                dataLabelsEnabled = false,
                xAxisLabelsEnabled = false,
                yAxisTitle = "",
                yAxisGridLineWidth = 0f,
                axesTextColor = "#78909C"
            ).apply {
                series(
                    arrayOf(
                        AASeriesElement().name("X").data(viewModel.xValues.toTypedArray()),
                        AASeriesElement().name("Y").data(viewModel.yValues.toTypedArray()),
                        AASeriesElement().name("Z").data(viewModel.zValues.toTypedArray())
                    )
                )
            }
            chart.aa_drawChartWithChartModel(chartModel)
        }
    }

    /**
     * Requests the notification permission.
     */
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    /* do nothing */
                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS) -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.all_notification)
                        .setMessage(R.string.notification_permission_rationale)
                        .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            dialogInterface?.dismiss()
                        }
                        .setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
                            dialogInterface?.dismiss()
                        }
                        .show()
                }

                else -> permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}