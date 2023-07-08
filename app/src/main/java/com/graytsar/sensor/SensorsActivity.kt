package com.graytsar.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.graytsar.sensor.databinding.ActivitySensorsBinding
import com.graytsar.sensor.utils.ARG_SENSOR_TYPE
import com.graytsar.sensor.utils.Globals
import com.graytsar.sensor.utils.keyPreferenceTheme
import com.graytsar.sensor.utils.keyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SensorsActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    private var _binding: ActivitySensorsBinding? = null
    private val binding get() = _binding!!

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivitySensorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.includeToolbar.toolbar
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration =
            AppBarConfiguration(setOf(R.id.fragmentSensors), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.navView, navController)

        initDrawerMenu(binding.navView)

        navController.addOnDestinationChangedListener(this)
        binding.navView.setNavigationItemSelectedListener { item: MenuItem ->
            onNavigationItemSelected(item)
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        //if(destination.id == R.id.fragmentHome) {
        //val intent = Intent(this, ForegroundServiceLogging::class.java)
        //stopService(intent)
        //}
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initDrawerMenu(navView: NavigationView) {
        val darkMode = (navView.menu.findItem(R.id.menuDarkMode)).actionView as MaterialCheckBox
        darkMode.isChecked = Globals.isNightMode
        darkMode.setOnClickListener {
            onMenuDarkModeClick(darkMode)
        }
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val sensorName = item.title.toString()

        when (item.itemId) {
            R.id.menuDarkMode -> {
                val darkMode = item.actionView as MaterialCheckBox
                onMenuDarkModeClick(darkMode)
            }

            R.id.accelerometer -> openDetail(sensorName, Sensor.TYPE_ACCELEROMETER)
            R.id.magneticField -> openDetail(sensorName, Sensor.TYPE_MAGNETIC_FIELD)
            R.id.gravity -> openDetail(sensorName, Sensor.TYPE_GRAVITY)
            R.id.gyroscope -> openDetail(sensorName, Sensor.TYPE_GYROSCOPE)
            R.id.linearAcceleration -> openDetail(sensorName, Sensor.TYPE_LINEAR_ACCELERATION)
            R.id.temperature -> openDetail(sensorName, Sensor.TYPE_AMBIENT_TEMPERATURE)
            R.id.light -> openDetail(sensorName, Sensor.TYPE_LIGHT)
            R.id.pressure -> openDetail(sensorName, Sensor.TYPE_PRESSURE)
            R.id.relativeHumidity -> openDetail(sensorName, Sensor.TYPE_RELATIVE_HUMIDITY)
            R.id.rotationVector -> openDetail(sensorName, Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)
            R.id.proximity -> openDetail(sensorName, Sensor.TYPE_PROXIMITY)
            R.id.stepCounter -> openDetail(sensorName, Sensor.TYPE_STEP_COUNTER)
            else -> Unit
        }
        drawerLayout.close()
        return true
    }

    private fun onMenuDarkModeClick(checkBox: MaterialCheckBox) {
        val sharedPref = this.getSharedPreferences(keyPreferenceTheme, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        if (Globals.isNightMode) {
            editor.putBoolean(keyTheme, false)
            editor.apply()
            Globals.isNightMode = false
            checkBox.isChecked = false
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        } else {
            editor.putBoolean(keyTheme, true)
            editor.apply()
            Globals.isNightMode = true
            checkBox.isChecked = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun openDetail(sensorName: String, sensorType: Int) {
        val navController: NavController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment).navController

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if (sensorManager.getDefaultSensor(sensorType) != null) {
            val bundle = Bundle().apply {
                putInt(ARG_SENSOR_TYPE, sensorType)
            }
            navController.navigate(R.id.fragmentDetail, bundle)
        } else {
            Snackbar.make(
                binding.root,
                "$sensorName ${getString(R.string.sensor_not_available)}",
                Snackbar.LENGTH_LONG
            ).show()
        }
    }
}