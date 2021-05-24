package utils

object Constants {

    const val NOTIFICATION_CHANNEL_ID = "SyrfLocationService"
    const val NOTIFICATION_ID = 1001
    private const val PACKAGE_NAME = "com.syrf.location"

    const val ACTION_LOCATION_BROADCAST = "$PACKAGE_NAME.action.LOCATION_BROADCAST"
    const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"
    const val EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION =
        "$PACKAGE_NAME.extra.CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION"
}