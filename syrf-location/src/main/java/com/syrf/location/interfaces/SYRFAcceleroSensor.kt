package com.syrf.location.interfaces

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.syrf.location.configs.SYRFAccelerometerConfig
import com.syrf.location.services.SYRFAcceleroTrackingService
import java.lang.Exception
import kotlin.jvm.Throws

/**
 * The interface class that exported to the client. You can use methods from this interface
 * to get update of Accelerometer sensor. Note that need to call configure method before using it
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
 * The singleton, implementation of [SYRFAcceleroSensorInterface] class. This will bind a service
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
     *
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
    }

    override fun getConfig(): SYRFAccelerometerConfig {
        checkConfig()
        return config
    }


    override fun subscribeToSensorDataUpdates(
        context: Activity,
        noAccelerometerSensorCallback: () -> Unit
    ) {
        acceleroTrackingService?.subscribeToSensorDataUpdates(
            context,
            noAccelerometerSensorCallback
        )
    }

    override fun unsubscribeToSensorDataUpdates() {
        acceleroTrackingService?.unsubscribeToSensorDataUpdates()
    }

    override fun onStop(context: Context) {
        if (isServiceBound) {
            context.unbindService(acceleroServiceConnection)
            isServiceBound = false
        }
    }

    // Monitors connection to the while-in-use service.
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

    @Throws(Exception::class)
    private fun checkConfig() {
        if (!this::config.isInitialized) {
            throw Exception("Config should be set before library use")
        }
    }
}