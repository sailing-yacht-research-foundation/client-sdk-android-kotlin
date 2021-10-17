package com.syrf.location.interfaces

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.syrf.location.configs.SYRFRotationConfig
import com.syrf.location.services.SYRFRotationTrackingService
import com.syrf.location.utils.NoConfigException
import com.syrf.location.utils.SDKValidator
import java.lang.Exception
import kotlin.jvm.Throws

/**
 * The interface class that exported to the client. You can use methods from this interface
 * to get update of Rotation sensor. Note that need to call configure method before using
 * any another methods
 */
interface SYRFRotationSensorInterface {
    fun configure(context: Activity)
    fun configure(config: SYRFRotationConfig, context: Activity)
    fun getConfig(): SYRFRotationConfig
    fun subscribeToSensorDataUpdates(context: Activity, noRotationSensorCallback: () -> Unit)
    fun unsubscribeToSensorDataUpdates()
    fun onStop(context: Context)
}

/**
 * The singleton, implementation of [SYRFRotationSensorInterface]. This will bind a service
 * called [SYRFRotationTrackingService] and start and stop request Rotation sensor data update
 * using this service
 */
object SYRFRotationSensor : SYRFRotationSensorInterface {
    private var rotationTrackingService: SYRFRotationTrackingService? = null
    private lateinit var config: SYRFRotationConfig
    private var isServiceBound = false

    /**
     * Configure the Rotation Service using default config.
     * The method should be called before any class usage
     * @param context The context. Should be the activity
     */
    override fun configure(context: Activity) {
        this.configure(SYRFRotationConfig.DEFAULT, context)
    }

    /**
     * Configure the Rotation Service. The method should be called before any class usage
     * @param config Configuration object
     * @param context The context. Should be the activity
     */
    override fun configure(config: SYRFRotationConfig, context: Activity) {
        SDKValidator.checkForApiKey(context)

        SYRFRotationSensor.config = config

        val serviceIntent = Intent(context, SYRFRotationTrackingService::class.java)
        context.bindService(
            serviceIntent,
            rotationServiceConnection, Context.BIND_AUTO_CREATE
        )

        isServiceBound = true

        SYRFTimber.i("SYRFRotationSensor configured")
    }

    /**
     * Check for initialization of config and return initialized value
     */
    override fun getConfig(): SYRFRotationConfig {
        checkConfig()
        return config
    }

    /**
     * Subscribe to sensor data update
     * @param context The context. Should be the activity
     * @param noRotationSensorCallback The callback will be executed when the
     * Rotation sensor is not available on the device
     */
    override fun subscribeToSensorDataUpdates(
        context: Activity,
        noRotationSensorCallback: () -> Unit
    ) {
        rotationTrackingService?.subscribeToSensorDataUpdates(
            context,
            noRotationSensorCallback
        )
    }

    /**
     * Unsubscribe to sensor data update
     */
    override fun unsubscribeToSensorDataUpdates() {
        rotationTrackingService?.unsubscribeToSensorDataUpdates()
    }

    /**
     * Should be called in onStop method of the activity that subscribed to data update
     * @param context The context. Should be the activity
     */
    override fun onStop(context: Context) {
        if (isServiceBound) {
            context.unbindService(rotationServiceConnection)
            isServiceBound = false
        }
    }

    /**
     * Monitors connection to the while-in-use service.
     */
    private val rotationServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as SYRFRotationTrackingService.LocalBinder
            rotationTrackingService = binder.service
        }

        override fun onServiceDisconnected(name: ComponentName) {
            rotationTrackingService = null
            isServiceBound = false
        }
    }

    /**
     * Check for config and throw an exception if it is not initialized
     * @throws NoConfigException
     */
    @Throws(Exception::class)
    private fun checkConfig() {
        if (!this::config.isInitialized) {
            SYRFTimber.e("Config should be set before library use")
            throw NoConfigException()
        }
    }
}