package com.syrf.testapp.activities

import android.Manifest
import android.app.Activity
import android.content.*
import android.location.Location
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.syrf.testapp.services.TimeService
import com.syrf.location.configs.SYRFLocationConfig
import com.syrf.location.configs.SYRFPermissionRequestConfig
import com.syrf.location.data.SYRFLocationData
import com.syrf.location.interfaces.SYRFLocation
import com.syrf.location.permissions.PermissionsManager
import com.syrf.time.configs.SYRFTimeConfig
import com.syrf.location.utils.Constants.ACTION_LOCATION_BROADCAST
import com.syrf.location.utils.Constants.EXTRA_LOCATION
import com.syrf.location.utils.MissingLocationException
import com.syrf.testapp.R
import com.syrf.testapp.SharedPreferenceUtil
import com.syrf.testapp.databinding.ActivityLocationBinding
import com.syrf.testapp.toText
import com.syrf.time.interfaces.SYRFTime

class LocationActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var locationBroadcastReceiver: LocationBroadcastReceiver
    private lateinit var binding: ActivityLocationBinding

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { result: Map<String, Boolean> ->
            val isAllGranted = result.entries.any { it.value }
            if (isAllGranted) {
                successOnPermissionsRequest.invoke()
            } else {
                failOnPermissionsRequest.invoke()
            }
        }

    private var successOnPermissionsRequest: () -> Unit = {}
    private var failOnPermissionsRequest: () -> Unit = {}

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, LocationActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        locationBroadcastReceiver = LocationBroadcastReceiver()

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
    }

    override fun onStart() {
        super.onStart()

        updateButtonState(
            sharedPreferences.getBoolean(SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)
        )
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val config = SYRFLocationConfig.Builder()
            .updateInterval(1)
            .maximumLocationAccuracy(SYRFLocationConfig.PRIORITY_HIGH_ACCURACY)
            .set()
        SYRFLocation.configure(config, this)

        val timeConfig = SYRFTimeConfig.Builder()
            .set()

        SYRFTime.configure(timeConfig, this)

        setupBtn()
    }

    override fun onPause() {
        super.onPause()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            locationBroadcastReceiver
        )
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            locationBroadcastReceiver,
            IntentFilter(
                ACTION_LOCATION_BROADCAST
            )
        )
    }

    override fun onStop() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        SYRFLocation.onStop(this)
        super.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        // Updates button states if new while in use location is added to SharedPreferences.
        if (key == SharedPreferenceUtil.KEY_FOREGROUND_ENABLED) {
            sharedPreferences?.getBoolean(
                SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false
            )?.let {
                updateButtonState(
                    it
                )
            }
        }
    }

    private fun updateButtonState(trackingLocation: Boolean) {
        binding.subscribeToPositionUpdateBtn.text = if (trackingLocation) {
            getString(R.string.stop_location_updates_button_text)
        } else {
            getString(R.string.start_location_updates_button_text)
        }
    }

    private fun requestCurrentPosition() {
        SYRFLocation.getCurrentPosition(this) { location, error ->
            if (location != null) {
                logResultsToScreen("${TimeService.currentTime()} - ${location.toText()}")
            }
            if (error is MissingLocationException) {
                successOnPermissionsRequest = {
                    requestCurrentPosition()
                }
                requestLocationPermission()
            }
        }
    }

    private fun setupBtn() {
        binding.getCurrentPositionBtn.setOnClickListener {
            requestCurrentPosition()
        }
        binding.subscribeToPositionUpdateBtn.setOnClickListener {
            val enabled = sharedPreferences.getBoolean(
                SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false
            )
            if (enabled) {
                SYRFLocation.unsubscribeToLocationUpdates()
            } else {
                SYRFLocation.subscribeToLocationUpdates(this) { _, error ->
                    if (error is MissingLocationException) {
                        successOnPermissionsRequest = {
                            SYRFLocation.subscribeToLocationUpdates(this)
                        }
                        requestLocationPermission()
                    }
                }
            }
            SharedPreferenceUtil.saveLocationTrackingPref(this, !enabled)
        }
    }

    private fun requestLocationPermission() {
        val config: SYRFPermissionRequestConfig = SYRFPermissionRequestConfig.getDefault(this)
        PermissionsManager(this).showPermissionReasonAndRequest(
            config,
            onPositionClick = {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            onNegativeClick = {}
        )
    }

    private fun logResultsToScreen(output: String) {
        val outputWithPreviousLogs = "$output\n${binding.outputTextView.text}"
        binding.outputTextView.text = outputWithPreviousLogs
    }

    private inner class LocationBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<SYRFLocationData>(
                EXTRA_LOCATION
            )

            if (location != null) {
                logResultsToScreen("${TimeService.currentTime()} - Battery level: ${location.batteryLevel} - ${location.toText()}")
            }
        }
    }
}