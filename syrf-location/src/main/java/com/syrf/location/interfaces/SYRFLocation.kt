package com.syrf.location.interfaces

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.syrf.location.services.SYRFLocationTrackingService
import com.syrf.location.configs.SYRFLocationConfig
import com.syrf.location.configs.SYRFPermissionRequestConfig
import com.syrf.location.permissions.PermissionsManager
import com.syrf.location.utils.CurrentPositionUpdateCallback
import java.lang.Exception
import kotlin.jvm.Throws

/**
 * The interface class that exported to the client. You can use methods from this interface
 * to get update of device's location. Note that need to call configure method before using
 * any another methods
 */
interface SYRFLocationInterface {
    fun configure(context: Activity)
    fun configure(config: SYRFLocationConfig, context: Activity)
    fun getCurrentPosition(context: Activity, callback: CurrentPositionUpdateCallback)
    fun getLocationConfig(): SYRFLocationConfig
    fun subscribeToLocationUpdates(context: Activity)
    fun unsubscribeToLocationUpdates()
    fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray,
        context: Activity
    )
    fun onStop(context: Context)
}

/**
 * The singleton, implementation of [SYRFLocationInterface] class. This will bind a service
 * called [SYRFLocationTrackingService] and start and stop request location data update
 * using this service
 */
object SYRFLocation : SYRFLocationInterface {
    private var locationTrackingService: SYRFLocationTrackingService? = null
    private lateinit var config: SYRFLocationConfig
    private var successOnPermissionsRequest: () -> Unit = {}
    private var failOnPermissionsRequest: () -> Unit = {}

    private var isLocationServiceBound = false

    /**
     * Configure the Location Service using default config.
     * The method should be called before any class usage
     * @param context The context. Should be the activity
     */
    override fun configure(context: Activity) {
        this.configure(SYRFLocationConfig.DEFAULT, context)
    }

    /**
     * Configure the Location Service. The method should be called before any class usage
     * @param config Configuration object
     * @param context The context. Should be the activity
     */
    override fun configure(config: SYRFLocationConfig, context: Activity) {
        SYRFLocation.config = config

        val serviceIntent = Intent(context, SYRFLocationTrackingService::class.java)
        context.bindService(
            serviceIntent,
            locationServiceConnection, Context.BIND_AUTO_CREATE
        )

        isLocationServiceBound = true
    }

    /**
     * Check for initialization of location config and return initialized value
     */
    override fun getLocationConfig(): SYRFLocationConfig {
        checkConfig()
        return config
    }

    /**
     * Get device's current location
     * @param context The context. Should be the activity
     * @param callback Callback will get called when request location complete
     */
    override fun getCurrentPosition(context: Activity, callback: CurrentPositionUpdateCallback) {
        checkConfig()
        successOnPermissionsRequest =
            { locationTrackingService?.getCurrentPosition(context, callback) }
        if (areLocationPermissionsGranted(context)) {
            successOnPermissionsRequest()
        } else {
            showPermissionReasonAndRequest(context)
        }
    }

    /**
     * Subscribe to device's location update
     * @param context The context. Should be the activity
     */
    override fun subscribeToLocationUpdates(context: Activity) {
        successOnPermissionsRequest =
            { locationTrackingService?.subscribeToLocationUpdates(context) }
        if (areLocationPermissionsGranted(context)) {
            successOnPermissionsRequest()
        } else {
            showPermissionReasonAndRequest(context)
        }
    }

    /**
     * Unsubscribe to device's location update
     */
    override fun unsubscribeToLocationUpdates() {
        locationTrackingService?.unsubscribeToLocationUpdates()
    }

    /**
     * Handle the result from requesting permissions
     * @param requestCode The request code passed when requested permissions.
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either PERMISSION_GRANTED or PERMISSION_DENIED
     * @param context The context. Should be the activity
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        context: Activity
    ) {
        val permissionsManager = PermissionsManager(context)
        permissionsManager.handleResults(
            permissions,
            successOnPermissionsRequest,
            failOnPermissionsRequest
        )
    }

    /**
     * Should be called in onStop method of the activity that subscribed to data update
     * @param context The context. Should be the activity
     */
    override fun onStop(context: Context) {
        if (isLocationServiceBound) {
            context.unbindService(locationServiceConnection)
            isLocationServiceBound = false
        }
    }

    /**
     * Monitors connection to the while-in-use service.
     */
    private val locationServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as SYRFLocationTrackingService.LocalBinder
            locationTrackingService = binder.service
        }

        override fun onServiceDisconnected(name: ComponentName) {
            locationTrackingService = null
            isLocationServiceBound = false
        }
    }

    /**
     * Check for location permission granting status.
     * @param context The context. Should be the activity
     */
    private fun areLocationPermissionsGranted(context: Activity): Boolean {
        val permissionsManager = PermissionsManager(context)
        val accessFineLocationGranted =
            permissionsManager.isPermissionGranted(android.Manifest.permission.ACCESS_FINE_LOCATION)
        val accessCoarseLocationGranted =
            permissionsManager.isPermissionGranted(android.Manifest.permission.ACCESS_COARSE_LOCATION)

        return accessFineLocationGranted && accessCoarseLocationGranted;
    }

    /**
     * Show an reason and request location permission request dialog using [PermissionsManager].
     * @param context The context. Should be the activity
     */
    private fun showPermissionReasonAndRequest(context: Activity) {
        val permissionsManager = PermissionsManager(context)
        permissionsManager.showPermissionReasonAndRequest(
            config.permissionRequestConfig ?: SYRFPermissionRequestConfig.getDefault(context),
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            1
        )
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