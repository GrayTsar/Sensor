package com.graytsar.sensor

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.google.android.material.checkbox.MaterialCheckBox
import com.graytsar.sensor.databinding.ActivityMainBinding
import com.graytsar.sensor.utils.ARG_SENSOR_TYPE
import com.graytsar.sensor.utils.StaticValues
import com.graytsar.sensor.utils.keyPreferenceTheme
import com.graytsar.sensor.utils.keyTheme

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {

        val mask = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)
        val sharedPref = this.getSharedPreferences(keyPreferenceTheme, Context.MODE_PRIVATE)

        when (mask){
            Configuration.UI_MODE_NIGHT_YES -> {
                StaticValues.isNightMode = true
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                if(sharedPref.getBoolean(keyTheme, false)){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    StaticValues.isNightMode = true
                } else {
                    StaticValues.isNightMode = false
                }
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> {
                StaticValues.isNightMode = false
            }
        }

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.includeToolbar.toolbar
        setSupportActionBar(toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navHostFragment: NavHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.fragmentHome), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        NavigationUI.setupWithNavController(binding.navView, navController)

        initDrawerMenu(binding.navView)

        navController.addOnDestinationChangedListener(this)
        binding.navView.setNavigationItemSelectedListener { item ->
            val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

            when(item.itemId) {
                R.id.menuDarkMode -> {
                    val darkMode = item.actionView as MaterialCheckBox
                    onMenuDarkModeClick(darkMode)
                }
                R.id.accelerometer -> {
                    if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_ACCELEROMETER)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    } else {
                        Snackbar.make(binding.root, "${item.title.toString()} ${getString(R.string.missingSensorBottomBar)}", Snackbar.LENGTH_LONG).show()
                    }
                }
                R.id.magneticField -> {
                    if(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_MAGNETIC_FIELD)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    } else {
                        Snackbar.make(binding.root, "${item.title.toString()} ${getString(R.string.missingSensorBottomBar)}", Snackbar.LENGTH_LONG).show()
                    }
                }
                R.id.gravity -> {
                    if(sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_GRAVITY)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    } else {
                        Snackbar.make(binding.root, "${item.title.toString()} ${getString(R.string.missingSensorBottomBar)}", Snackbar.LENGTH_LONG).show()
                    }
                }
                R.id.gyroscope -> {
                    if(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null) {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_GYROSCOPE)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    } else {
                        Snackbar.make(binding.root, "${item.title.toString()} ${getString(R.string.missingSensorBottomBar)}", Snackbar.LENGTH_LONG).show()
                    }
                }
                R.id.linearAcceleration -> {
                    if(sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_LINEAR_ACCELERATION)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    } else {
                        Snackbar.make(binding.root, "${item.title.toString()} ${getString(R.string.missingSensorBottomBar)}", Snackbar.LENGTH_LONG).show()
                    }
                }
                R.id.ambient_Temperature -> {
                    if(sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_AMBIENT_TEMPERATURE)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    } else {
                        Snackbar.make(binding.root, "${item.title.toString()} ${getString(R.string.missingSensorBottomBar)}", Snackbar.LENGTH_LONG).show()
                    }
                }
                R.id.light -> {
                    if(sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null) {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_LIGHT)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    } else {
                        Snackbar.make(binding.root, "${item.title.toString()} ${getString(R.string.missingSensorBottomBar)}", Snackbar.LENGTH_LONG).show()
                    }
                }
                R.id.pressure -> {
                    if(sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) != null) {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_PRESSURE)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    } else {
                        Snackbar.make(binding.root, "${item.title.toString()} ${getString(R.string.missingSensorBottomBar)}", Snackbar.LENGTH_LONG).show()
                    }
                }
                R.id.relativeHumidity -> {
                    if(sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null) {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_RELATIVE_HUMIDITY)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    } else {
                        Snackbar.make(binding.root, "${item.title.toString()} ${getString(R.string.missingSensorBottomBar)}", Snackbar.LENGTH_LONG).show()
                    }
                }
                R.id.geomagneticRotationVector -> {
                    if(sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) != null) {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    } else {
                        Snackbar.make(binding.root, "${item.title.toString()} ${getString(R.string.missingSensorBottomBar)}", Snackbar.LENGTH_LONG).show()
                    }
                }
                R.id.proximity -> {
                    if(sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) != null) {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_PROXIMITY)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    } else {
                        Snackbar.make(binding.root, "${item.title.toString()} ${getString(R.string.missingSensorBottomBar)}", Snackbar.LENGTH_LONG).show()
                    }
                }
                R.id.stepCounter -> {
                    if(sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) != null) {
                        val bundle = Bundle().apply {
                            putInt(ARG_SENSOR_TYPE, Sensor.TYPE_STEP_COUNTER)
                        }
                        navController.navigate(R.id.fragmentDetail, bundle)
                    } else {
                        Snackbar.make(binding.root, "${item.title.toString()} ${getString(R.string.missingSensorBottomBar)}", Snackbar.LENGTH_LONG).show()
                    }
                }
                else -> {

                }
            }

            drawerLayout.close()
            true
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestinationChanged(controller: NavController, destination: NavDestination, arguments: Bundle?) {
        if(destination.id == R.id.fragmentHome) {
            val intent = Intent(this, ForegroundServiceLogging::class.java)
            stopService(intent)
        }
    }

    private fun initDrawerMenu(navView: NavigationView) {
        val darkMode = (navView.menu.findItem(R.id.menuDarkMode)).actionView as MaterialCheckBox

        darkMode.isChecked = StaticValues.isNightMode

        darkMode.setOnClickListener {
            onMenuDarkModeClick(darkMode)
        }
    }

    private fun onMenuDarkModeClick(checkBox: MaterialCheckBox){
        val sharedPref = this.getSharedPreferences(keyPreferenceTheme, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        if(StaticValues.isNightMode){

            editor.putBoolean(keyTheme, false)
            editor.apply()

            StaticValues.isNightMode = false
            checkBox.isChecked = false
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        } else {
            editor.putBoolean(keyTheme, true)
            editor.apply()

            StaticValues.isNightMode = true
            checkBox.isChecked = true
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        }
    }
}