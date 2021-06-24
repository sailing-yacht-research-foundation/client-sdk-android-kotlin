package com.syrf.location.interfaces

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.syrf.location.configs.SYRFGyroscopeConfig
import com.syrf.location.services.SYRFGyroscopeTrackingService
import java.lang.Exception
import kotlin.jvm.Throws

/**
 * The interface class that exported to the client. You can use methods from this interface
 * to get update of Gyroscope sensor. Note that need to call configure method before using
 * any another methods
 */
interface SYRFGyroscopeSensorInterface {
    fun configure(context: Activity)
    fun configure(config: SYRFGyroscopeConfig, context: Activity)
    fun getConfig(): SYRFGyroscopeConfig
    fun subscribeToSensorDataUpdates(context: Activity, noGyroscopeSensorCallback: () -> Unit)
    fun unsubscribeToSensorDataUpdates()
    fun onStop(context: Context)
}

/**
 * The singleton, implementation of [SYRFGyroscopeSensorInterface]. This will bind a service
 * called [SYRFGyroscopeTrackingService] and start and stop request Gyroscope sensor data update
 * using this service
 */
object SYRFGyroscopeSensor : SYRFGyroscopeSensorInterface {
    private var gyroscopeTrackingService: SYRFGyroscopeTrackingService? = null
    private lateinit var config: SYRFGyroscopeConfig
    private var isServiceBound = false

    /**
     * Configure the Gyroscope Service using default config.
     * The method should be called before any class usage
     * @param context The context. Should be the activity
     */
    override fun configure(context: Activity) {
        this.configure(SYRFGyroscopeConfig.DEFAULT, context)
    }

    /**
     * Configure the Gyroscope Service. The method should be called before any class usage
     * @param config Configuration object
     * @param context The context. Should be the activity
     */
    override fun configure(config: SYRFGyroscopeConfig, context: Activity) {
        SYRFGyroscopeSensor.config = config

        val serviceIntent = Intent(context, SYRFGyroscopeTrackingService::class.java)
        context.bindService(
            serviceIntent,
            gyroscopeServiceConnection, Context.BIND_AUTO_CREATE
        )

        isServiceBound = true
    }

    /**
     * Check for initialization of config and return initialized value
     */
    override fun getConfig(): SYRFGyroscopeConfig {
        checkConfig()
        return config
    }

    /**
     * Subscribe to sensor data update
     * @param context The context. Should be the activity
     * @param noGyroscopeSensorCallback The callback will be executed when the
     * Gyroscope sensor is not available on the device
     */
    override fun subscribeToSensorDataUpdates(
        context: Activity,
        noGyroscopeSensorCallback: () -> Unit
    ) {
        gyroscopeTrackingService?.subscribeToSensorDataUpdates(
            context,
            noGyroscopeSensorCallback
        )
    }

    /**
     * Unsubscribe to sensor data update
     */
    override fun unsubscribeToSensorDataUpdates() {
        gyroscopeTrackingService?.unsubscribeToSensorDataUpdates()
    }

    /**
     * Should be called in onStop method of the activity that subscribed to data update
     * @param context The context. Should be the activity
     */
    override fun onStop(context: Context) {
        if (isServiceBound) {
            context.unbindService(gyroscopeServiceConnection)
            isServiceBound = false
        }
    }

    /**
     * Monitors connection to the while-in-use service.
     */
    private val gyroscopeServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as SYRFGyroscopeTrackingService.LocalBinder
            gyroscopeTrackingService = binder.service
        }

        override fun onServiceDisconnected(name: ComponentName) {
            gyroscopeTrackingService = null
            isServiceBound = false
        }
    }

    /**
     * Check for config and throw an exception if it is not initialized
     * @throws Exception
     */
    @Throws(Exception::class)
    private fun checkConfig() {
        if (!this::config.isInitialized) {
            throw Exception("Config should be set before library use")
        }
    }
}