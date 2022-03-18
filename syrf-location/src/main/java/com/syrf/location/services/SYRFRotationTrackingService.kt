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
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.syrf.location.R
import com.syrf.location.data.SYRFRotationSensorData
import com.syrf.location.interfaces.SYRFRotationSensor
import com.syrf.location.utils.Constants
import com.syrf.location.utils.Constants.EXTRA_CANCEL_ROTATION_SENSOR_TRACKING_FROM_NOTIFICATION
import com.syrf.location.utils.Constants.ROTATION_NOTIFICATION_CHANNEL_ID
import com.syrf.location.utils.Constants.ROTATION_NOTIFICATION_ID
import com.syrf.location.utils.serviceIsRunningInForeground


/**
 * The service using to request rotation sensor data update. It running in two modes:
 * background: when the activity that bind it is running, in this mode whe do not show notification
 * foreground: when the activity that bind it is destroyed, in this mode whe need to show a notification
 */
open class SYRFRotationTrackingService : Service(), SensorEventListener {

    private var currentSensorData: SYRFRotationSensorData? = null
    private lateinit var notificationManager: NotificationManager
    private lateinit var sensorManager: SensorManager
    private var sensorRotation: Sensor? = null

    private val localBinder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Get rotation sensor from the sensor manager.
        // The getDefaultSensor() method returns null if the sensor is not available on the device.
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorRotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val cancelTracking =
            intent?.getBooleanExtra(EXTRA_CANCEL_ROTATION_SENSOR_TRACKING_FROM_NOTIFICATION, false)

        if (cancelTracking == true) {
            unsubscribeToSensorDataUpdates()
        }

        // Tells the system not to recreate the service after it's been killed.
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        stopForeground(true)
        return localBinder
    }

    override fun onRebind(intent: Intent?) {
        // Activity (client) returns to the foreground and rebinds to service, so the service
        // can become a background services.
        stopForeground(true)
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        generateNotification(currentSensorData)?.let {
            startForeground(ROTATION_NOTIFICATION_ID, it)
        }
        return true
    }

    /**
     * Must be implemented to satisfy the SensorEventListener interface;
     * unused for now.
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onSensorChanged(event: SensorEvent?) {
        val rotationValues = event?.values ?: return

        val sensorRotationData =
            SYRFRotationSensorData(
                x = rotationValues[0],
                y = rotationValues[1],
                z = rotationValues[2],
                s = rotationValues[3],
                timestamp = System.currentTimeMillis(),
            )

        currentSensorData = sensorRotationData

        val intent = Intent(Constants.ACTION_ROTATION_SENSOR_BROADCAST)
        intent.putExtra(Constants.EXTRA_ROTATION_SENSOR_DATA, sensorRotationData)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

        if (serviceIsRunningInForeground(this::class.java)) {
            notificationManager.notify(
                ROTATION_NOTIFICATION_ID,
                generateNotification(sensorRotationData)
            )
        }
    }

    fun subscribeToSensorDataUpdates(
        context: Context,
        noRotationSensorCallback: () -> Unit
    ) {
        val notNullSensorRotation = sensorRotation ?: run {
            noRotationSensorCallback.invoke()
            return
        }

        val config = SYRFRotationSensor.getConfig()
        startService(Intent(context, SYRFRotationTrackingService::class.java))
        sensorManager.registerListener(
            this, notNullSensorRotation,
            config.sensorDelay
        )
    }

    fun unsubscribeToSensorDataUpdates() {
        sensorManager.unregisterListener(this)
        stopSelf()
    }

    private fun generateNotification(sensorSensorData: SYRFRotationSensorData?): Notification? {

        val mainNotificationText = sensorSensorData?.toText() ?: return null

        val titleText = getString(R.string.app_name)

        // 1. Create Notification Channel for O+ and beyond devices (26+).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                ROTATION_NOTIFICATION_CHANNEL_ID,
                "Rotation data update",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(mainNotificationText)
            .setBigContentTitle(titleText)

        val launchActivityIntent = Intent(this, this.javaClass)

        val cancelIntent = Intent(applicationContext, SYRFRotationTrackingService::class.java)
        cancelIntent.putExtra(EXTRA_CANCEL_ROTATION_SENSOR_TRACKING_FROM_NOTIFICATION, true)

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
            NotificationCompat.Builder(this, ROTATION_NOTIFICATION_CHANNEL_ID)

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
        internal val service: SYRFRotationTrackingService
            get() = this@SYRFRotationTrackingService
    }
}