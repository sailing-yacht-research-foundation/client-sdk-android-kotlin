package com.syrf.navigation.data

import android.location.Location
import android.os.Build
import android.os.Parcelable
import com.syrf.location.data.SYRFRotationSensorData
import kotlinx.parcelize.Parcelize

@Parcelize
data class SYRFNavigationData constructor(
    private val location: Location?,
    val sensorData: SYRFRotationSensorData?,
    val batteryLevel: Float,
) : Parcelable {
    /**
     * The latitude, in degrees.
     */
    val latitude: Double
        get() = location?.latitude ?: -1.0

    /**
     * The longitude, in degrees.
     */
    val longitude: Double
        get() = location?.longitude ?: -1.0

    /**
     * The altitude if available, in meters above the WGS 84 reference ellipsoid.
     * If this location does not have an altitude then 0.0 is returned.
     */
    val altitude: Double
        get() = location?.altitude ?: -1.0

    /**
     * The speed if it is available, in meters/second over ground.
     * If this location does not have a speed then 0.0 is returned.
     */
    val speed: Float
        get() = location?.speed ?: -1f

    /**
     * The estimated speed accuracy of this location, in meters per second.
     * If this location does not have a speed accuracy, then 0.0 is returned.
     */
    val speedAccuracy: Float
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            location?.speedAccuracyMetersPerSecond ?: -1f else 0F

    /**
     * The estimated horizontal accuracy of this location, radial, in meters.
     */
    val horizontalAccuracy: Float
        get() = location?.accuracy ?: -1f

    /**
     * The estimated vertical accuracy of this location, radial, in meters.
     */
    val verticalAccuracy: Float
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            location?.verticalAccuracyMeters ?: -1f else 0F


    /**
     * The horizontal direction of travel of this device in degrees east of true north.
     * It is guaranteed to be in the range (0.0, 360.0]. Reference:
     * https://stackoverflow.com/questions/4308262/calculate-compass-bearing-heading-to-location-in-android
     */
    val trueHeading: Float
        get() = location?.bearing ?: -1f


    /**
     * The UTC time of this fix, in milliseconds since January 1, 1970.
     */
    val timestamp: Long
        get() = location?.time ?: -1


    /**
     * Name of the provider, which provides location data.
     */
    val provider: String
        get() = location?.provider ?: ""


    /**
     * Get the estimated bearing accuracy of this location, in degrees.
     */
    val bearingAccuracy: Float
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            location?.bearingAccuracyDegrees ?: -1f else 0F

    public fun getLocation() = location
}