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

interface SYRFAcceleroSensorInterface {
    fun configure(context: Activity)
    fun configure(config: SYRFAccelerometerConfig, context: Activity)
    fun getConfig(): SYRFAccelerometerConfig
    fun subscribeToLocationUpdates(context: Activity, noAccelerometerSensorCallback: () -> Unit)
    fun unsubscribeToLocationUpdates()
    fun onStop(context: Context)
}

object SYRFAcceleroSensor : SYRFAcceleroSensorInterface {
    private var acceleroTrackingService: SYRFAcceleroTrackingService? = null
    private lateinit var config: SYRFAccelerometerConfig
    private var isServiceBound = false

    /**
     * Configure the Accelero Service using default config.
     * The method should be called before any class usage
     * @param context The context. Should be the activity
     */
    override fun configure(context: Activity) {
        configure(
            SYRFAccelerometerConfig.DEFAULT,
            context
        )
    }

    /**
     * Configure the Accelero Service. The method should be called before any class usage
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


    override fun subscribeToLocationUpdates(
        context: Activity,
        noAccelerometerSensorCallback: () -> Unit
    ) {
        acceleroTrackingService?.subscribeToAcceleroSensorUpdates(
            context,
            noAccelerometerSensorCallback
        )
    }

    override fun unsubscribeToLocationUpdates() {
        acceleroTrackingService?.unsubscribeToAcceleroSensorUpdates()
    }

    override fun onStop(context: Context) {
        if (isServiceBound) {
            acceleroTrackingService?.unsubscribeToAcceleroSensorUpdates()
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