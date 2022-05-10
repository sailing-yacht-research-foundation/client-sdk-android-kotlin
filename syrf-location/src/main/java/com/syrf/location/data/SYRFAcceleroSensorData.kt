package com.syrf.location.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

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

    /**
     * Convert acceleration data to text
     */
    fun toText(): String {
        return "(x-axis: $x m/s2, y-axis: $y m/s2, z-axis: $z) m/s2"
    }

    /**
     * The acceleration data in float array
     */
    val values: FloatArray
        get() = floatArrayOf(x, y, z)
}