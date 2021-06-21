package com.syrf.location.interfaces

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.syrf.location.configs.SYRFMagneticConfig
import com.syrf.location.services.SYRFMagneticTrackingService
import java.lang.Exception
import kotlin.jvm.Throws

/**
 * The interface class that exported to the client. You can use methods from this interface
 * to get update of Magnetic sensor. Note that need to call configure method before using
 * any another methods
 */
interface SYRFMagneticSensorInterface {
    fun configure(context: Activity)
    fun configure(config: SYRFMagneticConfig, context: Activity)
    fun getConfig(): SYRFMagneticConfig
    fun subscribeToSensorDataUpdates(context: Activity, noMagneticSensorCallback: () -> Unit)
    fun unsubscribeToSensorDataUpdates()
    fun onStop(context: Context)
}

/**
 * The singleton, implementation of [SYRFMagneticSensorInterface]. This will bind a service
 * called [SYRFMagneticTrackingService] and start and stop request Magnetic sensor data update
 * using this service
 */
object SYRFMagneticSensor : SYRFMagneticSensorInterface {
    private var magneticTrackingService: SYRFMagneticTrackingService? = null
    private lateinit var config: SYRFMagneticConfig
    private var isServiceBound = false

    /**
     * Configure the Magnetic Service using default config.
     * The method should be called before any class usage
     * @param context The context. Should be the activity
     */
    override fun configure(context: Activity) {
        this.configure(SYRFMagneticConfig.DEFAULT, context)
    }

    /**
     * Configure the Magnetic Service. The method should be called before any class usage
     * @param config Configuration object
     * @param context The context. Should be the activity
     */
    override fun configure(config: SYRFMagneticConfig, context: Activity) {
        SYRFMagneticSensor.config = config

        val serviceIntent = Intent(context, SYRFMagneticTrackingService::class.java)
        context.bindService(
            serviceIntent,
            magneticServiceConnection, Context.BIND_AUTO_CREATE
        )

        isServiceBound = true
    }

    /**
     * Check for initialization of config and return initialized value
     */
    override fun getConfig(): SYRFMagneticConfig {
        checkConfig()
        return config
    }

    /**
     * Subscribe to sensor data update
     * @param context The context. Should be the activity
     * @param noMagneticSensorCallback The callback will be executed when the
     * Magnetic sensor is not available on the device
     */
    override fun subscribeToSensorDataUpdates(
        context: Activity,
        noMagneticSensorCallback: () -> Unit
    ) {
        magneticTrackingService?.subscribeToSensorDataUpdates(
            context,
            noMagneticSensorCallback
        )
    }

    /**
     * Unsubscribe to sensor data update
     */
    override fun unsubscribeToSensorDataUpdates() {
        magneticTrackingService?.unsubscribeToSensorDataUpdates()
    }

    /**
     * Should be called in onStop method of the activity that subscribed to data update
     * @param context The context. Should be the activity
     */
    override fun onStop(context: Context) {
        if (isServiceBound) {
            context.unbindService(magneticServiceConnection)
            isServiceBound = false
        }
    }

    /**
     * Monitors connection to the while-in-use service.
     */
    private val magneticServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as SYRFMagneticTrackingService.LocalBinder
            magneticTrackingService = binder.service
        }

        override fun onServiceDisconnected(name: ComponentName) {
            magneticTrackingService = null
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