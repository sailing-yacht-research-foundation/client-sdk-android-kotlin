import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.syrf.location.services.SYRFLocationTrackingService
import com.syrf.location.configs.SYRFLocationConfig
import com.syrf.location.permissions.PermissionsManager
import com.syrf.location.utils.CurrentPositionUpdateCallback
import java.lang.Exception
import kotlin.jvm.Throws

interface SYRFLocationInterface {
    fun configure(context: Activity)
    fun configure(config: SYRFLocationConfig, context: Activity)
    fun getCurrentPosition(context: Activity, callback: CurrentPositionUpdateCallback)
    fun getLocationConfig() : SYRFLocationConfig
    fun subscribeToLocationUpdates(context: Activity)
    fun unsubscribeToLocationUpdates()
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray,
                                   context: Activity)
    fun onStop(context: Context)
}


object SYRFLocation: SYRFLocationInterface {
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
     *
     * @param config Configuration object
     * @param context The context. Should be the activity
     */
    override fun configure(config: SYRFLocationConfig, context: Activity) {
        this.config = config

        val serviceIntent = Intent(context, SYRFLocationTrackingService::class.java)
        context.bindService(serviceIntent, locationServiceConnection, Context.BIND_AUTO_CREATE)

        isLocationServiceBound = true
    }

    override fun getLocationConfig(): SYRFLocationConfig {
        checkConfig()
        return config
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
        if (isLocationServiceBound) {
            context.unbindService(locationServiceConnection)
            isLocationServiceBound = false
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
            isLocationServiceBound = false
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
        if (!this::config.isInitialized) {
            throw Exception("Config should be set before library use")
        }
    }
}