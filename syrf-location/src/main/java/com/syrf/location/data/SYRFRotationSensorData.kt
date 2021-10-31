package com.syrf.location.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Represents a data return from Rotation sensor.
 * @property x Rotation vector component along the x axis
 * @property y Rotation vector component along the y axis
 * @property z Rotation vector component along the z axis
 * @property s Scalar component of the rotation vector
 * @property timestamp The timestamp at which the rotation data was determined
 */
@Parcelize
data class SYRFRotationSensorData constructor(
    val x: Float,
    val y: Float,
    val z: Float,
    val s: Float,
    val timestamp: Long
) : Parcelable {

    /**
     * Convert rotation data to text
     */
    fun toText(): String {
        return "(x: $x, y: $y, z: $z, s: $s )"
    }

    /**
     * The rotation data in float array
     */
    val values: FloatArray
        get() = floatArrayOf(x, y, z, s)
}