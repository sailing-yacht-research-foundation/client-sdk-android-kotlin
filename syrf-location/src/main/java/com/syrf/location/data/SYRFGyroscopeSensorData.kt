package com.syrf.location.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Represents a data return from Gyroscope sensor.
 * @property x The rate of rotation around the x-axis in rad/s
 * @property y The rate of rotation around the y-axis in rad/s
 * @property z The rate of rotation around the z-axis in rad/s
 * @property timestamp The timestamp at which the gyroscope data was determined
 */
@Parcelize
data class SYRFGyroscopeSensorData constructor(
    val x: Float,
    val y: Float,
    val z: Float,
    val timestamp: Long
) : Parcelable {

    fun toText(): String {
        return "(x-axis: $x rad/s, y-axis: $y rad/s, z-axis: $z rad/s)"
    }

    val values: FloatArray
        get() = floatArrayOf(x, y, z)
}