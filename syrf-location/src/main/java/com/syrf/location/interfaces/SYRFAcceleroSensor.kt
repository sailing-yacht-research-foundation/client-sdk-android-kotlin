package com.syrf.location.interfaces

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.syrf.core.interfaces.SYRFTimber
import com.syrf.location.configs.SYRFAccelerometerConfig
import com.syrf.location.services.SYRFAcceleroTrackingService
import com.syrf.location.services.SYRFLocationTrackingService
import java.lang.Exception
import kotlin.jvm.Throws

/**
 * The interface class that exported to the client. You can use methods from this interface
 * to get update of Accelerometer sensor. Note that need to call configure method before using
 * any another methods
 */
interface SYRFAcceleroSensorInterface {
    fun configure(context: Activity)
    fun configure(config: SYRFAccelerometerConfig, context: Activity)
    fun getConfig(): SYRFAccelerometerConfig
    fun subscribeToSensorDataUpdates(context: Activity, noAccelerometerSensorCallback: () -> Unit)
    fun unsubscribeToSensorDataUpdates()
    fun onStop(context: Context)
}

/**
 * The singleton, implementation of [SYRFAcceleroSensorInterface]. This will bind a service
 * called [SYRFAcceleroTrackingService] and start and stop request Accelerometer sensor data update
 * using this service
 */
object SYRFAcceleroSensor : SYRFAcceleroSensorInterface {
    private var acceleroTrackingService: SYRFAcceleroTrackingService? = null
    private lateinit var config: SYRFAccelerometerConfig
    private var isServiceBound = false

    /**
     * Configure the Accelerometer Service using default config.
     * The method should be called before any class usage
     * @param context The context. Should be the activity
     */
    override fun configure(context: Activity) {
        this.configure(SYRFAccelerometerConfig.DEFAULT, context)
    }

    /**
     * Configure the Accelerometer Service. The method should be called before any class usage
     * @param config Configuration object
     * @param context The context. Should be the activity
     */
    override fun configure(config: SYRFAccelerometerConfig, context: Activity) {
        SYRFAcceleroSensor.config = config

        val serviceIntent = Intent(context, SYRFAcceleroTrackingService::class.java)
        context.bindService(
            serviceIntent,
            acceleroServiceConnection, Context.BIND_AUTO_CREATE
        )

        isServiceBound = true

        SYRFTimber.i("SYRFAcceleroSensor configured")
    }

    /**
     * Check for initialization of config and return initialized value
     */
    override fun getConfig(): SYRFAccelerometerConfig {
        checkConfig()
        return config
    }

    /**
     * Subscribe to sensor data update
     * @param context The context. Should be the activity
     * @param noAccelerometerSensorCallback The callback will be executed when the
     * Accelerometer sensor is not available on the device
     */
    override fun subscribeToSensorDataUpdates(
        context: Activity,
        noAccelerometerSensorCallback: () -> Unit
    ) {
        acceleroTrackingService?.subscribeToSensorDataUpdates(
            context,
            noAccelerometerSensorCallback
        )
    }

    /**
     * Unsubscribe to sensor data update
     */
    override fun unsubscribeToSensorDataUpdates() {
        acceleroTrackingService?.unsubscribeToSensorDataUpdates()
    }

    /**
     * Should be called in onStop method of the activity that subscribed to data update
     * @param context The context. Should be the activity
     */
    override fun onStop(context: Context) {
        if (isServiceBound) {
            context.unbindService(acceleroServiceConnection)
            isServiceBound = false
        }
    }

    /**
     * Monitors connection to the while-in-use service.
     */
    private val acceleroServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as SYRFAcceleroTrackingService.LocalBinder
            acceleroTrackingService = binder.service
        }

        override fun onServiceDisconnected(name: ComponentName) {
            acceleroTrackingService = null
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
            SYRFTimber.e("Config should be set before library use")
            throw Exception("Config should be set before library use")
        }
    }
}