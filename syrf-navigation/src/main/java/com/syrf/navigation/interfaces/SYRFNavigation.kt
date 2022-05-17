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
import com.syrf.navigation.data.SYRFToggler
import com.syrf.navigation.receivers.LocationBroadcastReceiver
import com.syrf.navigation.receivers.RotationBroadcastReceiver
import kotlinx.coroutines.*

interface SYRFNavigationInterface {
    fun configure(config: SYRFNavigationConfig, activity: Activity)

    fun subscribeToNavigationUpdates(
        activity: Activity,
        callback: SubscribeToLocationUpdateCallback?
    )

    fun unsubscribeToNavigationUpdates(activity: Activity)

    fun getCurrentPosition(activity: Activity, callback: CurrentPositionUpdateCallback)

    fun onAppMoveToBackground(activity: Activity)
    fun onAppMoveToForeground(activity: Activity)

    fun updateNavigationSettings(
        toggler: SYRFToggler,
        activity: Activity,
        callback: SubscribeToLocationUpdateCallback?
    )

    fun updateThrottle(throttle: Long)
}

object SYRFNavigation : SYRFNavigationInterface {
    private val locationBroadcastReceiver = LocationBroadcastReceiver()
    private val rotationBroadcastReceiver = RotationBroadcastReceiver()

    private var isRegisterLocationReceiver = false
    private var isRegisterRotationReceiver = false

    var location: SYRFLocationData? = null
    var sensorData: SYRFRotationSensorData? = null

    private var throttleTimeForeground: Long = 1_000
    private var throttleTimeBackground: Long = 2_000
    private var throttleTime: Long = 1_000
    var throttleJob: Job? = null

    private var localBroadcastManager: LocalBroadcastManager? = null
    private var batteryService: BatteryManager? = null

    private var config: SYRFNavigationConfig? = null
    private var toggler: SYRFToggler? = null

    @Override
    override fun configure(config: SYRFNavigationConfig, activity: Activity) {
        this.config = config
        configureLocation(config.locationConfig, activity)
        configureRotation(config.headingConfig, activity)

        batteryService = activity.getSystemService(BATTERY_SERVICE) as BatteryManager
        throttleTimeForeground = config.throttleForegroundDelay.toLong()
        throttleTimeBackground = config.throttleBackgroundDelay.toLong()
        localBroadcastManager = LocalBroadcastManager.getInstance(activity.applicationContext)
    }

    @Override
    override fun subscribeToNavigationUpdates(
        activity: Activity,
        callback: SubscribeToLocationUpdateCallback?
    ) {
        if (config?.locationConfig?.enabled == true) {
            subscribeToLocationUpdates(activity, callback)
        }
        if (config?.headingConfig?.enabled == true) {
            subscribeToSensorDataUpdates(activity)
        }
    }

    @Override
    override fun unsubscribeToNavigationUpdates(activity: Activity) {
        unsubscribeToLocationUpdates(activity)
        unsubscribeToSensorDataUpdates(activity)
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

    fun processUpdate() {
        if (location !== null && sensorData === null && toggler?.heading == true) {
            return
        }
        if (location === null && sensorData !== null && toggler?.location == true) {
            return
        }

        throttleFirst(throttleTime, GlobalScope, ::fireUpdate)
    }

    private fun fireUpdate() {
        val intent = Intent(Constants.ACTION_NAVIGATION_BROADCAST)
        intent.putExtra(Constants.EXTRA_NAVIGATION, prepareData())
        localBroadcastManager?.sendBroadcast(intent)
    }

    private fun prepareData(): SYRFNavigationData {
        val deviceInfo = if (this.config?.deviceInfoConfig === null) null
        else {
            val batteryLevel = batteryService?.let {
                return@let it.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
                    .toDouble() / 100
            } ?: -1.0
            SYRFDeviceInfoData(
                batteryInfo = batteryLevel,
                osVersion = SYRFDeviceInfo.getOsVersion(),
                deviceModel = SYRFDeviceInfo.getPhoneModel(),
            )
        }
        return SYRFNavigationData(location, sensorData, deviceInfo)
    }

    fun throttleFirst(
        skipMs: Long = 300L,
        coroutineScope: CoroutineScope,
        destinationFunction: () -> Unit
    ) {
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                destinationFunction()
                delay(skipMs)
            }
        }
    }

    private fun updateThrottleTime(isBackground: Boolean) {
        throttleTime = if (isBackground) throttleTimeBackground else throttleTimeForeground
    }

    @Override
    override fun getCurrentPosition(activity: Activity, callback: CurrentPositionUpdateCallback) {
        SYRFLocation.getCurrentPosition(activity, callback)
    }

    @Override
    override fun onAppMoveToBackground(activity: Activity) {
        updateThrottleTime(true)
        SYRFLocation.onStop(activity)
        SYRFRotationSensor.onStop(activity)
    }

    @Override
    override fun onAppMoveToForeground(activity: Activity) {
        updateThrottleTime(false)
    }

    @Override
    override fun updateNavigationSettings(
        toggler: SYRFToggler,
        activity: Activity,
        callback: SubscribeToLocationUpdateCallback?
    ) {
        this.toggler = toggler

        val locationConfigBuilder = SYRFLocationConfig.Builder()
        locationConfigBuilder.enable(toggler.location ?: false)
        configureLocation(locationConfigBuilder.set(), activity)

        if (toggler.location == true) {
            subscribeToLocationUpdates(activity, callback)
        } else {
            unsubscribeToLocationUpdates(activity)
        }
        val rotationConfigBuilder = SYRFRotationConfig.Builder()
        rotationConfigBuilder.enable(toggler.heading ?: false)
        configureRotation(rotationConfigBuilder.set(), activity)

        if (toggler.heading == true) {
            subscribeToSensorDataUpdates(activity)
        } else {
            unsubscribeToSensorDataUpdates(activity)
        }
    }

    @Override
    override fun updateThrottle(throttle: Long) {
        if (throttle != throttleTime) {
            throttleTime = throttle
        }
    }

    private fun subscribeToLocationUpdates(
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

    private fun unsubscribeToLocationUpdates(activity: Activity) {
        SYRFLocation.unsubscribeToLocationUpdates()
        if (isRegisterLocationReceiver) {
            LocalBroadcastManager.getInstance(activity)
                .unregisterReceiver(locationBroadcastReceiver)
            isRegisterLocationReceiver = false
        }
    }

    private fun subscribeToSensorDataUpdates(activity: Activity) {
        SYRFRotationSensor.subscribeToSensorDataUpdates(activity) {}
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

    private fun unsubscribeToSensorDataUpdates(activity: Activity) {
        SYRFRotationSensor.unsubscribeToSensorDataUpdates()
        if (isRegisterRotationReceiver) {
            LocalBroadcastManager.getInstance(activity)
                .unregisterReceiver(rotationBroadcastReceiver)
            isRegisterRotationReceiver = false
        }
    }
}
