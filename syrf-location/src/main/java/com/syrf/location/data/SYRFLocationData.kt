package com.syrf.location.data

import android.location.Location
import android.os.Build
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Represents the data that will be exported to client from an instance of location.
 * @property location The location
 */
@Parcelize
class SYRFLocationData constructor(private val location: Location) : Parcelable {

    /**
     * The latitude, in degrees.
     */
    val latitude: Double
        get() = location.latitude

    /**
     * The longitude, in degrees.
     */
    val longitude: Double
        get() = location.longitude

    /**
     * The altitude if available, in meters above the WGS 84 reference ellipsoid.
     * If this location does not have an altitude then 0.0 is returned.
     */
    val altitude: Double
        get() = location.altitude

    /**
     * The speed if it is available, in meters/second over ground.
     * If this location does not have a speed then 0.0 is returned.
     */
    val speed: Float
        get() = location.speed

    /**
     * The estimated speed accuracy of this location, in meters per second.
     * If this location does not have a speed accuracy, then 0.0 is returned.
     */
    val speedAccuracy: Float
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            location.speedAccuracyMetersPerSecond else 0F

    /**
     * The estimated horizontal accuracy of this location, radial, in meters.
     */
    val horizontalAccuracy: Float
        get() = location.accuracy

    /**
     * The estimated vertical accuracy of this location, radial, in meters.
     */
    val verticalAccuracy: Float
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            location.verticalAccuracyMeters else 0F


    /**
     * The horizontal direction of travel of this device in degrees east of true north.
     * It is guaranteed to be in the range (0.0, 360.0]. Reference:
     * https://stackoverflow.com/questions/4308262/calculate-compass-bearing-heading-to-location-in-android
     */
    val trueHeading: Float
        get() = location.bearing

    /**
     * The UTC time of this fix, in milliseconds since January 1, 1970.
     */
    val timestamp: Long
        get() = location.time
}