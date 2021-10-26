package com.syrf.location.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Represents a data return from Magnetic sensor.
 * @property x The azimuth
 * @property y The pitch
 * @property z The roll
 * @property timestamp
 */
@Parcelize
data class SYRFRotationData constructor(
    val azimuth: Float,
    val pitch: Float,
    val roll: Float,
    val timestamp: Long
) : Parcelable {

    /**
     * Convert rotation data to text
     */
    fun toText(): String {
        return "(x: $azimuth, y: $pitch, z: $roll)"
    }

    /**
     * The rotation data in float array
     */
    val values: FloatArray
        get() = floatArrayOf(azimuth, pitch, roll)
}