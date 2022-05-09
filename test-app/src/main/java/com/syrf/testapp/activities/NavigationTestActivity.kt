package com.syrf.testapp.activities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.syrf.location.configs.SYRFPermissionRequestConfig
import com.syrf.location.data.SYRFLocationData
import com.syrf.location.data.SYRFRotationSensorData
import com.syrf.location.interfaces.SYRFLocation
import com.syrf.location.permissions.PermissionsManager
import com.syrf.location.utils.Constants
import com.syrf.location.utils.MissingLocationException
import com.syrf.navigation.data.SYRFNavigationData
import com.syrf.navigation.interfaces.SYRFNavigation
import com.syrf.testapp.R
import com.syrf.testapp.services.TimeService
import com.syrf.testapp.toText

class NavigationTestActivity : AppCompatActivity() {
    private var successOnPermissionsRequest: () -> Unit = {}
    private var failOnPermissionsRequest: () -> Unit = {}

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result: Map<String, Boolean> ->
        val isAllGranted = result.entries.any { it.value }
        if (isAllGranted) {
            successOnPermissionsRequest.invoke()
        } else {
            failOnPermissionsRequest.invoke()
        }
    }

    private val navigationBroadcastReceiver = NavigationBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_test)

        findViewById<Button>(R.id.start_subscribe).setOnClickListener {
            SYRFNavigation.subscribeToLocationUpdates(this) { _, error ->
                if (error is MissingLocationException) {
                    successOnPermissionsRequest = {
                        SYRFLocation.subscribeToLocationUpdates(this)
                    }
                    requestLocationPermission()
                }
            }
            SYRFNavigation.subscribeToSensorDataUpdates(this) {
                Log.d("NamH", "No sensor found")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("NamH", "onResume")

        // configure
        SYRFNavigation.configure(this)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            navigationBroadcastReceiver,
            IntentFilter(Constants.ACTION_NAVIGATION_BROADCAST)
        )
    }

    override fun onStop() {
        Log.d("NamH", "onStop")
        SYRFNavigation.unsubscribeToLocationUpdates(this)
        SYRFNavigation.unsubscribeToSensorDataUpdates(this)
        SYRFNavigation.onStop(this)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(navigationBroadcastReceiver)
        super.onStop()
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

    private inner class NavigationBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.getParcelableExtra<SYRFNavigationData>(Constants.EXTRA_NAVIGATION)?.let {
                Log.d("NamH", "Receive: $it")
            }
        }
    }
}