package com.syrf.navigation.interfaces

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.syrf.location.data.SYRFLocationData
import com.syrf.location.data.SYRFRotationSensorData
import com.syrf.location.interfaces.SYRFLocation
import com.syrf.location.interfaces.SYRFRotationSensor
import com.syrf.location.utils.Constants
import com.syrf.location.utils.Constants.EXTRA_ROTATION_SENSOR_DATA
import com.syrf.location.utils.CurrentPositionUpdateCallback
import com.syrf.location.utils.SubscribeToLocationUpdateCallback
import com.syrf.navigation.data.SYRFNavigationData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object SYRFNavigation {
    private val locationBroadcastReceiver = LocationBroadcastReceiver()
    private val rotationBroadcastReceiver = RotationBroadcastReceiver()

    private var isRegisterLocationReceiver = false
    private var isRegisterRotationReceiver = false

    private var location: SYRFLocationData? = null
    private var sensorData: SYRFRotationSensorData? = null

    private val delayTime: Long = 700
    private var job: Job? = null

    private var localBroadcastManager: LocalBroadcastManager? = null

    /// Location part
    fun configure(activity: Activity) {
        localBroadcastManager = LocalBroadcastManager.getInstance(activity.applicationContext)
        SYRFLocation.configure(activity)
        SYRFRotationSensor.configure(activity)

        job = GlobalScope.launch {
            while (true) {
                delay(delayTime)
                if (location != null && sensorData != null) {
                    val intent = Intent(Constants.ACTION_NAVIGATION_BROADCAST)
                    intent.putExtra(
                        Constants.EXTRA_NAVIGATION,
                        SYRFNavigationData(location?.pureLocation, sensorData, -1f)
                    )
                    localBroadcastManager?.sendBroadcast(intent)
                }
            }
        }
    }

    fun getCurrentPosition(activity: Activity, callback: CurrentPositionUpdateCallback) {
        SYRFLocation.getCurrentPosition(activity, callback)
    }

    fun subscribeToLocationUpdates(
        activity: Activity,
        callback: SubscribeToLocationUpdateCallback?
    ) {
        SYRFLocation.subscribeToLocationUpdates(activity, callback)
        if (!isRegisterLocationReceiver) {
            activity.let {
                LocalBroadcastManager.getInstance(it).registerReceiver(
                    locationBroadcastReceiver,
                    IntentFilter(Constants.ACTION_LOCATION_BROADCAST)
                )
                isRegisterLocationReceiver = true
            }
        }
    }

    fun unsubscribeToLocationUpdates(activity: Activity) {
        SYRFLocation.unsubscribeToLocationUpdates()
        if (isRegisterLocationReceiver) {
            LocalBroadcastManager.getInstance(activity)
                .unregisterReceiver(locationBroadcastReceiver)
            isRegisterLocationReceiver = false
        }
    }

    fun onStop(context: Context) {
        SYRFLocation.onStop(context)
        SYRFRotationSensor.onStop(context)
        job?.cancel()
    }

    /// Rotation part
    fun subscribeToSensorDataUpdates(
        activity: Activity,
        noRotationSensorCallback: () -> Unit
    ) {
        SYRFRotationSensor.subscribeToSensorDataUpdates(activity, noRotationSensorCallback)
        if (!isRegisterRotationReceiver) {
            activity.let {
                LocalBroadcastManager.getInstance(it).registerReceiver(
                    rotationBroadcastReceiver,
                    IntentFilter(Constants.ACTION_ROTATION_SENSOR_BROADCAST)
                )
                isRegisterRotationReceiver = true
            }
        }
    }

    fun unsubscribeToSensorDataUpdates(activity: Activity) {
        SYRFRotationSensor.unsubscribeToSensorDataUpdates()
        if (isRegisterRotationReceiver) {
            LocalBroadcastManager.getInstance(activity)
                .unregisterReceiver(rotationBroadcastReceiver)
            isRegisterRotationReceiver = false
        }
    }

    class LocationBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.getParcelableExtra<SYRFLocationData>(Constants.EXTRA_LOCATION)?.let {
                location = it
            }
        }
    }

    class RotationBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.getParcelableExtra<SYRFRotationSensorData>(EXTRA_ROTATION_SENSOR_DATA)?.let {
                sensorData = it
            }
        }
    }
}
