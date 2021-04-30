package com.syrf.testapp

import SYRFLocation
import android.content.*
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import config.SYRFLocationConfig
import kotlinx.android.synthetic.main.activity_main.*
import utils.Constants.ACTION_LOCATION_BROADCAST
import utils.Constants.EXTRA_LOCATION

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var locationBroadcastReceiver: LocationBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
                .updateInterval(30)
                .maximumLocationAccuracy(100)
                .set()
        SYRFLocation.configure(config, this)
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
                ACTION_LOCATION_BROADCAST)
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
        SYRFLocation.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        // Updates button states if new while in use location is added to SharedPreferences.
        if (key == SharedPreferenceUtil.KEY_FOREGROUND_ENABLED) {
            sharedPreferences?.getBoolean(
                SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)?.let {
                updateButtonState(
                    it
                )
            }
        }
    }


    private fun updateButtonState(trackingLocation: Boolean) {
        if (trackingLocation) {
            subscribe_to_position_update_btn.text = getString(R.string.stop_location_updates_button_text)
        } else {
            subscribe_to_position_update_btn.text = getString(R.string.start_location_updates_button_text)
        }
    }

    private fun setupBtn() {
        get_current_position_btn.setOnClickListener() {
            SYRFLocation.getCurrentPosition(this) { location, error ->
                if (location != null) {
                    logResultsToScreen("location: ${location.toText()}")
                }
            }
        }
        subscribe_to_position_update_btn.setOnClickListener() {
            val enabled = sharedPreferences.getBoolean(
                SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)
            if (enabled) {
                SYRFLocation.unsubscribeToLocationUpdates()
            } else {
                SYRFLocation.subscribeToLocationUpdates(this)
            }
            SharedPreferenceUtil.saveLocationTrackingPref(this, !enabled)
        }
    }

    private fun logResultsToScreen(output: String) {
        val outputWithPreviousLogs = "$output\n${output_text_view.text}"
        output_text_view.text = outputWithPreviousLogs
    }

    private inner class LocationBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(
                EXTRA_LOCATION
            )

            if (location != null) {
                logResultsToScreen("location: ${location.toText()}")
            }
        }
    }
}