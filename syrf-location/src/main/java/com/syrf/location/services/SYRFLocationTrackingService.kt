package com.syrf.location.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationListenerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.LocationRequest
import com.syrf.location.R
import com.syrf.location.data.SYRFLocationData
import com.syrf.location.interfaces.SYRFLocation
import com.syrf.location.interfaces.SYRFTimber
import com.syrf.location.utils.Constants.ACTION_LOCATION_BROADCAST
import com.syrf.location.utils.Constants.EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION
import com.syrf.location.utils.Constants.EXTRA_LOCATION
import com.syrf.location.utils.Constants.LOCATION_NOTIFICATION_ID
import com.syrf.location.utils.Constants.NOTIFICATION_CHANNEL_ID
import com.syrf.location.utils.CurrentPositionUpdateCallback
import com.syrf.location.utils.SubscribeToLocationUpdateCallback
import com.syrf.location.utils.toText
import java.util.concurrent.TimeUnit

@SuppressLint("MissingPermission")
open class SYRFLocationTrackingService : Service() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var locationRequest: LocationRequest

    private var currentLocation: Location? = null

    private var serviceRunningInForeground = false
    private var didRequestLocationUpdate = false

    private val localBinder = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val cancelLocationTrackingFromNotification =
            intent?.getBooleanExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, false)

        if (cancelLocationTrackingFromNotification == true) {
            unsubscribeToLocationUpdates()
            stopSelf()
            SYRFTimber.v("Stop by notification")
        }

        // Tells the system not to recreate the service after it's been killed.
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = LocationListenerCompat { location ->
            val intent = Intent(ACTION_LOCATION_BROADCAST)
            intent.putExtra(EXTRA_LOCATION, SYRFLocationData(location))
            LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

            if (serviceRunningInForeground) {
                notificationManager.notify(
                    LOCATION_NOTIFICATION_ID,
                    generateNotification(location)
                )
            }

            currentLocation = location
        }

        locationRequest = LocationRequest.create().apply {
            val config = SYRFLocation.getLocationConfig()

            interval = TimeUnit.SECONDS.toMillis(config.updateInterval)
            priority = config.maximumLocationAccuracy
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        stopForeground(true)
        serviceRunningInForeground = false
        return localBinder
    }

    override fun onRebind(intent: Intent?) {
        // LocationActivity (client) returns to the foreground and rebinds to service, so the service
        // can become a background services.
        stopForeground(true)
        serviceRunningInForeground = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        val notification = generateNotification(currentLocation)
        startForeground(LOCATION_NOTIFICATION_ID, notification)
        serviceRunningInForeground = true
        return true
    }

    fun getCurrentPosition(callback: CurrentPositionUpdateCallback) {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            locationManager.getLastKnownLocation(SYRFLocation.getLocationConfig().provider)?.let { location ->
                callback.invoke(SYRFLocationData(location), null)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                locationManager.getCurrentLocation(
                    SYRFLocation.getLocationConfig().provider,
                    null,
                    ContextCompat.getMainExecutor(this)
                ) { location ->
                    if (location != null) {
                        callback.invoke(SYRFLocationData(location), null)
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                locationManager.requestSingleUpdate(
                    SYRFLocation.getLocationConfig().provider,
                    locationListener,
                    Looper.getMainLooper()
                )
            }
        } catch (ex: Exception) {
            callback.invoke(null, ex)
        }
    }

    fun subscribeToLocationUpdates(callback: SubscribeToLocationUpdateCallback?) {
        if (didRequestLocationUpdate) return
        startService(Intent(this, SYRFLocationTrackingService::class.java))
        try {
            locationManager.requestLocationUpdates(
                SYRFLocation.getLocationConfig().provider,
                locationRequest.interval,
                MINIMUM_DISPLACEMENT_IN_METERS,
                locationListener,
                Looper.getMainLooper()
            )
            didRequestLocationUpdate = true
            callback?.invoke(Unit, null)
        } catch (ex: Exception) {
            callback?.invoke(null, ex)
        }
    }

    fun unsubscribeToLocationUpdates() {
        if (!didRequestLocationUpdate) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                locationManager.requestFlush(
                    SYRFLocation.getLocationConfig().provider,
                    locationListener,
                    FLUSH_COMPLETED
                )
            }
            locationManager.removeUpdates(locationListener)
            didRequestLocationUpdate = false
            stopSelf()
        } catch (ex: Exception) {
            SYRFTimber.e(ex)
        }
    }

    private fun generateNotification(location: Location?): Notification {
        val mainNotificationText = location?.toText() ?: getString(R.string.no_location_text)

        val titleText = getString(R.string.app_name)

        // 1. Create Notification Channel for O+ and beyond devices (26+).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Location update",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(mainNotificationText)
            .setBigContentTitle(titleText)

        val launchActivityIntent = Intent(this, this.javaClass)

        val cancelIntent = Intent(applicationContext, SYRFLocationTrackingService::class.java)
        cancelIntent.putExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, true)

        val servicePendingIntent = PendingIntent.getService(
            this,
            0,
            cancelIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val activityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchActivityIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notificationCompatBuilder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)

        return notificationCompatBuilder
            .setStyle(bigTextStyle)
            .setContentTitle(titleText)
            .setContentText(mainNotificationText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .setNotificationSilent()
            .addAction(
                0,
                getString(R.string.launch_activity),
                activityPendingIntent
            )
            .addAction(
                0,
                getString(R.string.stop_location_updates_button_text),
                servicePendingIntent
            )
            .build()
    }

    inner class LocalBinder : Binder() {
        internal val service: SYRFLocationTrackingService
            get() = this@SYRFLocationTrackingService
    }

    companion object {
        const val FLUSH_COMPLETED = 0
        const val MINIMUM_DISPLACEMENT_IN_METERS = 0f
    }
}
