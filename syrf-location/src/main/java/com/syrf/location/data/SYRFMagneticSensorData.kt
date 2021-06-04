package com.syrf.location.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Represents a data return from Magnetic sensor.
 * @property x The geomagnetic field strength along the x-axis in μT
 * @property y The geomagnetic field strength along the y-axis in μT
 * @property z The geomagnetic field strength along the z-axis in μT
 * @property timestamp The timestamp at which the accelerometer data was determined
 */
@Parcelize
data class SYRFMagneticSensorData constructor(
    val x: Float,
    val y: Float,
    val z: Float,
    val timestamp: Long
) : Parcelable {

    fun toText(): String {
        return "(x-axis: $x, y-axis: $y, z-axis: $z)"
    }
}