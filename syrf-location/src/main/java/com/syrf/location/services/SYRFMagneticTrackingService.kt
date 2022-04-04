package com.syrf.location.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.syrf.location.R
import com.syrf.location.data.SYRFMagneticSensorData
import com.syrf.location.interfaces.SYRFMagneticSensor
import com.syrf.location.utils.Constants
import com.syrf.location.utils.Constants.EXTRA_CANCEL_MAGNETIC_SENSOR_TRACKING_FROM_NOTIFICATION
import com.syrf.location.utils.Constants.MAGNETIC_NOTIFICATION_CHANNEL_ID
import com.syrf.location.utils.Constants.MAGNETIC_NOTIFICATION_ID

/**
 * The service using to request magnetic sensor data update. It running in two modes:
 * background: when the activity that bind it is running, in this mode whe do not show notification
 * foreground: when the activity that bind it is destroyed and flag usingForegroundService set
 * to true in config, in this mode whe need to show a notification
 */
open class SYRFMagneticTrackingService : Service(), SensorEventListener {

    private var currentSensorData: SYRFMagneticSensorData? = null
    private lateinit var notificationManager: NotificationManager
    private lateinit var sensorManager: SensorManager
    private var sensorMagnetic: Sensor? = null

    private val config = SYRFMagneticSensor.getConfig()

    private var serviceRunningInForeground = false

    private val localBinder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Get Magnetic sensor from the sensor manager.
        // The getDefaultSensor() method returns null if the sensor is not available on the device.
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorMagnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val cancelTracking =
            intent?.getBooleanExtra(EXTRA_CANCEL_MAGNETIC_SENSOR_TRACKING_FROM_NOTIFICATION, false)

        if (cancelTracking == true) {
            unsubscribeToSensorDataUpdates()
        }

        // Tells the system not to recreate the service after it's been killed.
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        if (config.usingForegroundService) {
            stopForeground(true)
            serviceRunningInForeground = false
        }

        return localBinder
    }

    override fun onRebind(intent: Intent?) {
        if (config.usingForegroundService) {
            // Activity (client) returns to the foreground and rebinds to service, so the service
            // can become a background services.
            stopForeground(true)
            serviceRunningInForeground = false
        }

        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        if (config.usingForegroundService) {
            generateNotification(currentSensorData)?.let {
                startForeground(MAGNETIC_NOTIFICATION_ID, it)
            }
            serviceRunningInForeground = true
        } else {
            unsubscribeToSensorDataUpdates()
        }

        return true
    }

    /**
     * Must be implemented to satisfy the SensorEventListener interface;
     * unused for now.
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val geomegneticValues = event?.values ?: return
        val sensorSensorData =
            SYRFMagneticSensorData(
                x = geomegneticValues[0],
                y = geomegneticValues[1],
                z = geomegneticValues[2],
                timestamp = System.currentTimeMillis()
            )

        currentSensorData = sensorSensorData

        val intent = Intent(Constants.ACTION_MAGNETIC_SENSOR_BROADCAST)
        intent.putExtra(Constants.EXTRA_MAGNETIC_SENSOR_DATA, sensorSensorData)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

        if (serviceRunningInForeground) {
            notificationManager.notify(
                MAGNETIC_NOTIFICATION_ID,
                generateNotification(sensorSensorData)
            )
        }
    }

    fun subscribeToSensorDataUpdates(
        context: Context,
        noMagneticSensorCallback: () -> Unit
    ) {
        val notNullSensorMagnetic = sensorMagnetic ?: run {
            noMagneticSensorCallback.invoke()
            return
        }

        val config = SYRFMagneticSensor.getConfig()
        startService(Intent(context, SYRFMagneticTrackingService::class.java))
        sensorManager.registerListener(
            this, notNullSensorMagnetic,
            config.sensorDelay
        )
    }

    fun unsubscribeToSensorDataUpdates() {
        sensorManager.unregisterListener(this)
        stopSelf()
    }

    private fun generateNotification(sensorSensorData: SYRFMagneticSensorData?): Notification? {

        val mainNotificationText = sensorSensorData?.toText() ?: return null

        val titleText = getString(R.string.app_name)

        // 1. Create Notification Channel for O+ and beyond devices (26+).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                MAGNETIC_NOTIFICATION_CHANNEL_ID,
                "Magnetic data update",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(mainNotificationText)
            .setBigContentTitle(titleText)

        val launchActivityIntent = Intent(this, this.javaClass)

        val cancelIntent = Intent(applicationContext, SYRFMagneticTrackingService::class.java)
        cancelIntent.putExtra(EXTRA_CANCEL_MAGNETIC_SENSOR_TRACKING_FROM_NOTIFICATION, true)

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
            NotificationCompat.Builder(this, MAGNETIC_NOTIFICATION_CHANNEL_ID)

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
                getString(R.string.stop_updates_button_text),
                servicePendingIntent
            )
            .build()
    }

    inner class LocalBinder : Binder() {
        internal val service: SYRFMagneticTrackingService
            get() = this@SYRFMagneticTrackingService
    }
}