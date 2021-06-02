package com.syrf.location.configs

import android.hardware.SensorManager

class SYRFAccelerometerConfig private constructor(
    val sensorDelay: Int,
) {

    companion object {
        val DEFAULT: SYRFAccelerometerConfig = SYRFAccelerometerConfig(sensorDelay = SensorManager.SENSOR_DELAY_NORMAL)
    }

    data class Builder(
        var sensorDelay: Int? = null,
    ) {
        fun sensorDelay(sensorDelay: Int) = apply { this.sensorDelay = sensorDelay }
        fun set() = SYRFAccelerometerConfig(sensorDelay ?: DEFAULT.sensorDelay)
    }
}
