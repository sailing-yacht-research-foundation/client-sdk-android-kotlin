package com.syrf.navigation.interfaces

import android.app.Activity
import android.content.Context.BATTERY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.syrf.device_info.data.SYRFDeviceInfoData
import com.syrf.device_info.interfaces.SYRFDeviceInfo
import com.syrf.location.configs.SYRFLocationConfig
import com.syrf.location.configs.SYRFRotationConfig
import com.syrf.location.data.SYRFLocationData
import com.syrf.location.data.SYRFRotationSensorData
import com.syrf.location.interfaces.SYRFLocation
import com.syrf.location.interfaces.SYRFRotationSensor
import com.syrf.location.utils.Constants
import com.syrf.location.utils.CurrentPositionUpdateCallback
import com.syrf.location.utils.SubscribeToLocationUpdateCallback
import com.syrf.navigation.data.SYRFNavigationConfig
import com.syrf.navigation.data.SYRFNavigationData
import com.syrf.navigation.receivers.LocationBroadcastReceiver
import com.syrf.navigation.receivers.RotationBroadcastReceiver
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

interface SYRFNavigationInterface {
    fun configure(config: SYRFNavigationConfig, activity: Activity)

    fun subscribeToLocationUpdates(
        activity: Activity,
        callback: SubscribeToLocationUpdateCallback?
    )

    fun unsubscribeToLocationUpdates(activity: Activity)

    fun subscribeToSensorDataUpdates(
        activity: Activity,
        noRotationSensorCallback: () -> Unit
    )

    fun unsubscribeToSensorDataUpdates(activity: Activity)

    fun getCurrentPosition(activity: Activity, callback: CurrentPositionUpdateCallback)

    fun onAppMoveToBackground(activity: Activity)
}

object SYRFNavigation : SYRFNavigationInterface {
    private val locationBroadcastReceiver = LocationBroadcastReceiver()
    private val rotationBroadcastReceiver = RotationBroadcastReceiver()

    private var isRegisterLocationReceiver = false
    private var isRegisterRotationReceiver = false

    var location: SYRFLocationData? = null
    var sensorData: SYRFRotationSensorData? = null

    private var throttleTime: Long = 1_000
    private var job: Job? = null

    private var localBroadcastManager: LocalBroadcastManager? = null
    private var batteryService: BatteryManager? = null

    @Override
    override fun configure(config: SYRFNavigationConfig, activity: Activity) {
        configureLocation(config.locationConfig, activity)
        configureRotation(config.headingConfig, activity)
        batteryService = activity.getSystemService(BATTERY_SERVICE) as BatteryManager
        throttleTime = config.throttleForegroundDelay.toLong()
        localBroadcastManager = LocalBroadcastManager.getInstance(activity.applicationContext)
        startEventLoop()
    }

    private fun configureLocation(config: SYRFLocationConfig?, activity: Activity) {
        config?.let {
            SYRFLocation.configure(it, activity)
        } ?: run {
            SYRFLocation.configure(activity)
        }
    }

    private fun configureRotation(config: SYRFRotationConfig?, activity: Activity) {
        config?.let {
            SYRFRotationSensor.configure(it, activity)
        } ?: run {
            SYRFRotationSensor.configure(activity)
        }
    }

    private fun startEventLoop() {
        job = GlobalScope.launch {
            while (true) {
                delay(throttleTime)
                if (location != null && sensorData != null) {
                    val batteryLevel = batteryService?.let {
                        return@let it.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                            .toDouble() / 100
                    } ?: -1.0
                    val deviceInfo = SYRFDeviceInfoData(
                        batteryInfo = batteryLevel,
                        osVersion = SYRFDeviceInfo.getOsVersion(),
                        deviceModel = SYRFDeviceInfo.getPhoneModel(),
                    )
                    val intent = Intent(Constants.ACTION_NAVIGATION_BROADCAST)
                    intent.putExtra(
                        Constants.EXTRA_NAVIGATION,
                        SYRFNavigationData(location, sensorData, deviceInfo)
                    )
                    localBroadcastManager?.sendBroadcast(intent)
                }
            }
        }
    }

    @Override
    override fun getCurrentPosition(activity: Activity, callback: CurrentPositionUpdateCallback) {
        SYRFLocation.getCurrentPosition(activity, callback)
    }

    @Override
    override fun onAppMoveToBackground(activity: Activity) {
        SYRFLocation.onStop(activity)
    }

    fun updateThrottle(throttle: Long) {
        if (throttle != throttleTime) {
            throttleTime = throttle
        }
    }

    @Override
    override fun subscribeToLocationUpdates(
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

    @Override
    override fun unsubscribeToLocationUpdates(activity: Activity) {
        SYRFLocation.unsubscribeToLocationUpdates()
        if (isRegisterLocationReceiver) {
            LocalBroadcastManager.getInstance(activity)
                .unregisterReceiver(locationBroadcastReceiver)
            isRegisterLocationReceiver = false
        }
    }

    @Override
    override fun subscribeToSensorDataUpdates(
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

    @Override
    override fun unsubscribeToSensorDataUpdates(activity: Activity) {
        SYRFRotationSensor.unsubscribeToSensorDataUpdates()
        if (isRegisterRotationReceiver) {
            LocalBroadcastManager.getInstance(activity)
                .unregisterReceiver(rotationBroadcastReceiver)
            isRegisterRotationReceiver = false
        }
    }
}
