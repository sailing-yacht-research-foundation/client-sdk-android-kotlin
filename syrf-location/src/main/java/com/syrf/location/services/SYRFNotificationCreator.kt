package com.syrf.location.interfaces

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.core.app.NotificationCompat
import com.syrf.location.R
import com.syrf.location.data.SYRFRotationSensorData
import com.syrf.location.services.SYRFLocationTrackingService
import com.syrf.location.utils.Constants
import com.syrf.location.utils.Constants.NAVIGATION_CHANNEL_ID
import com.syrf.location.utils.Constants.NAVIGATION_NOTIFICATION_ID
import com.syrf.location.utils.toText

object NotificationCreator {
    const val notificationId = NAVIGATION_NOTIFICATION_ID
    var location: Location? = null
    var heading: SYRFRotationSensorData? = null

    fun getNotification(locationData: Location?, headingData: SYRFRotationSensorData?, context: Context): Notification {
        if (locationData !== null) {
            location = locationData
        }
        if (headingData !== null) {
            heading = headingData
        }
        return generateNotification(location, heading, context)
    }
}

private fun generateNotification(location: Location?, heading: SYRFRotationSensorData?, context: Context): Notification {
    var mainNotificationText = ""
    if (location != null) {
        mainNotificationText += "Location: ${location.toText()} "
    }

    if (heading != null) {
        mainNotificationText += "Rotation: ${heading.toText()}"
    }

    val titleText = context.getString(com.syrf.location.R.string.app_name)

    // 1. Create Notification Channel for O+ and beyond devices (26+).
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationChannel = NotificationChannel(
            NAVIGATION_CHANNEL_ID,
            "Navigation update",
            NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(notificationChannel)
    }

    val bigTextStyle = NotificationCompat.BigTextStyle()
        .bigText(mainNotificationText)
        .setBigContentTitle(titleText)

    val launchActivityIntent = Intent(context, context.javaClass)

    val cancelIntent = Intent(context, SYRFLocationTrackingService::class.java)
    cancelIntent.putExtra(Constants.EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, true)

    val servicePendingIntent = PendingIntent.getService(
        context,
        0,
        cancelIntent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val activityPendingIntent = PendingIntent.getActivity(
        context,
        0,
        launchActivityIntent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val notificationCompatBuilder =
        NotificationCompat.Builder(context, NAVIGATION_CHANNEL_ID)

    return notificationCompatBuilder
        .setStyle(bigTextStyle)
        .setContentTitle(titleText)
        .setContentText(mainNotificationText)
        .setSmallIcon(com.syrf.location.R.mipmap.ic_launcher)
        .setOngoing(true)
        .setVisibility(NotificationCompat.VISIBILITY_SECRET)
        .addAction(
            0,
            context.getString(com.syrf.location.R.string.launch_activity),
            activityPendingIntent
        )
        .addAction(
            0,
            context.getString(com.syrf.location.R.string.stop_location_updates_button_text),
            servicePendingIntent
        )
        .build()
}