package com.syrf.location.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.syrf.location.R
import toText
import utils.Constants.ACTION_LOCATION_BROADCAST
import utils.Constants.EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION
import utils.Constants.EXTRA_LOCATION
import utils.Constants.NOTIFICATION_CHANNEL_ID
import utils.Constants.NOTIFICATION_ID
import utils.CurrentPositionUpdateCallback
import java.util.concurrent.TimeUnit

open class SYRFLocationTrackingService: Service() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null
    private lateinit var notificationManager: NotificationManager

    private var serviceRunningInForeground = false

    private val localBinder = LocalBinder()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val cancelLocationTrackingFromNotification =
            intent?.getBooleanExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, false)

        if (cancelLocationTrackingFromNotification == true) {
            unsubscribeToLocationUpdates()
            stopSelf()
        }

        // Tells the system not to recreate the service after it's been killed.

        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = TimeUnit.SECONDS.toMillis(60)
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    currentLocation = locationResult.lastLocation

                    val intent = Intent(ACTION_LOCATION_BROADCAST)
                    intent.putExtra(EXTRA_LOCATION, currentLocation)
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

                    if (serviceRunningInForeground) {
                        notificationManager.notify(
                            NOTIFICATION_ID,
                            generateNotification(currentLocation)
                        )
                    }
                }
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        stopForeground(true)
        serviceRunningInForeground = false
        return localBinder
    }

    override fun onRebind(intent: Intent?) {
        // MainActivity (client) returns to the foreground and rebinds to service, so the service
        // can become a background services.
        stopForeground(true)
        serviceRunningInForeground = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        val notification = generateNotification(currentLocation)
        startForeground(NOTIFICATION_ID, notification)
        serviceRunningInForeground = true
        return true
    }


    @SuppressLint("MissingPermission")
    fun getCurrentPosition(context: Context, callback: CurrentPositionUpdateCallback) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

        val cts = CancellationTokenSource()
        val cancellationToken = cts.token
        cancellationToken.onCanceledRequested(OnTokenCanceledListener {
            // TODO:
        })

        fusedLocationProviderClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationToken
        ).addOnSuccessListener { location -> callback.invoke(location, null) }
            .addOnFailureListener { exception -> callback.invoke(null, exception) }
    }

    fun subscribeToLocationUpdates(context: Context) {
        startService(Intent(context, SYRFLocationTrackingService::class.java))

        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        } catch (unlikely: SecurityException) {
            // TODO: add catch message
        }
    }

    fun unsubscribeToLocationUpdates() {
        try {
            val removeTask = fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    stopSelf()
                } else {
                    // TODO: add failed message
                }
            }
        } catch (unlikely: SecurityException) {
            // TODO: add catch message
        }
    }

    private fun generateNotification(location: Location?): Notification {

        val mainNotificationText = location?.toText() ?: getString(R.string.no_location_text)

        val titleText = getString(R.string.app_name)

        // 1. Create Notification Channel for O+ and beyond devices (26+).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID, "Location update", NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(mainNotificationText)
            .setBigContentTitle(titleText)

        val launchActivityIntent = Intent(this, this.javaClass)

        val cancelIntent = Intent(applicationContext, NOTIFICATION_CHANNEL_ID::class.java)
        cancelIntent.putExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, true)

        val servicePendingIntent = PendingIntent.getService(
            this,
            0,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val activityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchActivityIntent,
            0
        )

        val notificationCompatBuilder =
                NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)

        return notificationCompatBuilder
                .setStyle(bigTextStyle)
                .setContentTitle(titleText)
                .setContentText(mainNotificationText)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setOngoing(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
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
}