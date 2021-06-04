package com.syrf.location.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Represents a data return from Accelerometer sensor.
 * @property x The acceleration on the x-axis in m/s2
 * @property y The acceleration on the y-axis in m/s2
 * @property z The acceleration on the z-axis in m/s2
 * @property timestamp The timestamp at which the accelerometer data was determined
 */
@Parcelize
data class SYRFAcceleroSensorData constructor(
    val x: Float,
    val y: Float,
    val z: Float,
    val timestamp: Long
) : Parcelable {

    fun toText(): String {
        return "(x-axis: $x, y-axis: $y, z-axis: $z)"
    }
}