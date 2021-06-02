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
import com.syrf.location.data.SYRFSensorsAcceleroData
import com.syrf.location.utils.Constants
import com.syrf.location.utils.Constants.ACCELERO_NOTIFICATION_CHANNEL_ID
import com.syrf.location.utils.Constants.ACCELERO_NOTIFICATION_ID
import com.syrf.location.utils.Constants.EXTRA_CANCEL_ACCELERO_SENSOR_TRACKING_FROM_NOTIFICATION

open class SYRFAcceleroTrackingService : Service(), SensorEventListener {

    private var currentSensorAcceleroData: SYRFSensorsAcceleroData? = null
    private lateinit var notificationManager: NotificationManager
    private lateinit var sensorManager: SensorManager
    private var sensorAccelerometer: Sensor? = null


    private var serviceRunningInForeground = false

    private val localBinder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        // Get accelerometer sensor from the sensor manager.
        // The getDefaultSensor() method returns null if the sensor is not available on the device.
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val cancelTracking =
            intent?.getBooleanExtra(EXTRA_CANCEL_ACCELERO_SENSOR_TRACKING_FROM_NOTIFICATION, false)

        if (cancelTracking == true) {
            unsubscribeToAcceleroSensorUpdates()
        }

        // Tells the system not to recreate the service after it's been killed.
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        stopForeground(true)
        serviceRunningInForeground = false
        return localBinder
    }

    override fun onRebind(intent: Intent?) {
        // Activity (client) returns to the foreground and rebinds to service, so the service
        // can become a background services.
        stopForeground(true)
        serviceRunningInForeground = false
        super.onRebind(intent)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        generateNotification(currentSensorAcceleroData)?.let {
            startForeground(ACCELERO_NOTIFICATION_ID, it)
        }
        serviceRunningInForeground = true
        return true
    }

    /**
     * Must be implemented to satisfy the SensorEventListener interface;
     * unused for now.
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val axisValues = event?.values ?: return
        val sensorAcceleroData =
            SYRFSensorsAcceleroData(
                x = axisValues[0],
                y = axisValues[1],
                z = axisValues[2],
                timestamp = SYRFTime.getCurrentTimeMS()
            )

        currentSensorAcceleroData = sensorAcceleroData

        val intent = Intent(Constants.ACTION_ACCELERO_SENSOR_BROADCAST)
        intent.putExtra(Constants.EXTRA_ACCELERO_SENSOR_DATA, sensorAcceleroData)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)

        if (serviceRunningInForeground) {
            notificationManager.notify(
                Constants.LOCATION_NOTIFICATION_ID,
                generateNotification(sensorAcceleroData)
            )
        }
    }

    fun subscribeToAcceleroSensorUpdates(
        context: Context,
        noAccelerometerSensorCallback: () -> Unit
    ) {
        val sensorAccelerometer = sensorAccelerometer ?: run{
            noAccelerometerSensorCallback.invoke()
            return
        }

        startService(Intent(context, SYRFAcceleroTrackingService::class.java))
        sensorManager.registerListener(
            this, sensorAccelerometer,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun unsubscribeToAcceleroSensorUpdates() {
        sensorManager.unregisterListener(this)
        stopSelf()
    }

    private fun generateNotification(sensorData: SYRFSensorsAcceleroData?): Notification? {

        val mainNotificationText = sensorData?.toString() ?: return null

        val titleText = getString(R.string.app_name)

        // 1. Create Notification Channel for O+ and beyond devices (26+).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                ACCELERO_NOTIFICATION_CHANNEL_ID,
                "Accelerometer data update",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }

        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(mainNotificationText)
            .setBigContentTitle(titleText)

        val launchActivityIntent = Intent(this, this.javaClass)

        val cancelIntent = Intent(applicationContext, ACCELERO_NOTIFICATION_CHANNEL_ID::class.java)
        cancelIntent.putExtra(EXTRA_CANCEL_ACCELERO_SENSOR_TRACKING_FROM_NOTIFICATION, true)

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
            NotificationCompat.Builder(this, ACCELERO_NOTIFICATION_CHANNEL_ID)

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
                getString(R.string.stop_updates_button_text),
                servicePendingIntent
            )
            .build()
    }

    inner class LocalBinder : Binder() {
        internal val service: SYRFAcceleroTrackingService
            get() = this@SYRFAcceleroTrackingService
    }
}