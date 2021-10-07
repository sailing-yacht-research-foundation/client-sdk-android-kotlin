package com.syrf.location.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Represents a data return from Magnetic sensor.
 * @property x The azimuth
 * @property y The pitch
 * @property z The roll
 * @property timestamp The timestamp at which the rotation data was determined
 */
@Parcelize
data class SYRFRotationSensorData constructor(
    val x: Float,
    val y: Float,
    val z: Float,
    val timestamp: Long
) : Parcelable {

    /**
     * Convert magnetic data to text
     */
    fun toText(): String {
        return "(x: $x, y: $y, z: $z )"
    }

    /**
     * The magnetic data in float array
     */
    val values: FloatArray
        get() = floatArrayOf(x, y, z)
}