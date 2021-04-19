import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.syrf.syrflocationlibrary.services.SYRFLocationTrackingService
import config.SYRFLocationConfig
import permissions.PermissionsManager
import utils.CurrentPositionUpdateCallback
import java.lang.Exception

interface SYRFLocationInterface {
    fun configure(config: SYRFLocationConfig, context: Activity)
    fun getCurrentPosition(context: Activity, callback: CurrentPositionUpdateCallback)
    fun subscribeToLocationUpdates(context: Activity)
    fun unsubscribeToLocationUpdates()
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray,
                                   context: Activity)
    fun onStop(context: Context)
}

object SYRFLocation: SYRFLocationInterface {
    private var locationTrackingService: SYRFLocationTrackingService? = null
    private var config: SYRFLocationConfig? = null
    private var successOnPermissionsRequest: () -> Unit = {}
    private var failOnPermissionsRequest: () -> Unit = {}

    private var LocationServiceBound = false

    override fun configure(config: SYRFLocationConfig, context: Activity) {
        this.config = config

        val serviceIntent = Intent(context, SYRFLocationTrackingService::class.java)
        context.bindService(serviceIntent, locationServiceConnection, Context.BIND_AUTO_CREATE)

        LocationServiceBound = true
    }

    override fun getCurrentPosition(context: Activity, callback: CurrentPositionUpdateCallback) {
        checkConfig()
        successOnPermissionsRequest = { locationTrackingService?.getCurrentPosition(context, callback) }
        if (areLocationPermissionsGranted(context)) {
            successOnPermissionsRequest()
        } else {
            showPermissionReasonAndRequest(context)
        }
    }

    override fun subscribeToLocationUpdates(context: Activity) {
        if (areLocationPermissionsGranted(context)) {
            locationTrackingService?.subscribeToLocationUpdates(context)
        } else {
            showPermissionReasonAndRequest(context)
        }
    }

    override fun unsubscribeToLocationUpdates() {
        // TODO("Not yet implemented")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        context: Activity
    ) {
        val permissionsManager = PermissionsManager(context)
        permissionsManager.handleResults(permissions, successOnPermissionsRequest, failOnPermissionsRequest)
    }

    override fun onStop(context: Context) {
        if (LocationServiceBound) {
            context.unbindService(locationServiceConnection)
            LocationServiceBound = false
        }
    }

    // Monitors connection to the while-in-use service.
    private val locationServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as SYRFLocationTrackingService.LocalBinder
            locationTrackingService = binder.service
        }

        override fun onServiceDisconnected(name: ComponentName) {
            locationTrackingService = null
            LocationServiceBound = false
        }
    }

    private fun areLocationPermissionsGranted(context: Activity): Boolean {
        val permissionsManager = PermissionsManager(context)
        val accessFineLocationGranted = permissionsManager.isPermissionGranted(android.Manifest.permission.ACCESS_FINE_LOCATION)
        val accessCoarseLocationGranted = permissionsManager.isPermissionGranted(android.Manifest.permission.ACCESS_COARSE_LOCATION)

        return accessFineLocationGranted && accessCoarseLocationGranted;
    }

    private fun showPermissionReasonAndRequest(context: Activity) {
        val permissionsManager = PermissionsManager(context)

        // TODO: get info from the config object
        permissionsManager.showPermissionReasonAndRequest(
            "Permissions",
            "Need the access to the location",
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
            1)
    }

    @Throws(Exception::class)
    private fun checkConfig() {
        if (config === null) {
            throw Exception("Config should be set before library use")
        }
    }
}