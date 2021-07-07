package com.syrf.location.configs

import android.hardware.SensorManager

/**
 * The class help you config params of Accelerometer sensor request
 * @property sensorDelay The rate of sensor request frequency. Should be one of
 * [SensorManager.SENSOR_DELAY_FASTEST],
 * [SensorManager.SENSOR_DELAY_GAME],
 * [SensorManager.SENSOR_DELAY_UI],
 * [SensorManager.SENSOR_DELAY_NORMAL]
 */
class SYRFAccelerometerConfig private constructor(
    val sensorDelay: Int,
) {

    companion object {
        /**
         * Provide a default config for using in cases client init the SDK
         * without config or missing some properties in config
         */
        val DEFAULT: SYRFAccelerometerConfig =
            SYRFAccelerometerConfig(sensorDelay = SensorManager.SENSOR_DELAY_NORMAL)
    }

    /**
     * Builder class that help to create an instance of [SYRFAccelerometerConfig]
     */
    data class Builder(
        var sensorDelay: Int? = null,
    ) {
        fun sensorDelay(sensorDelay: Int) = apply { this.sensorDelay = sensorDelay }
        fun set() = SYRFAccelerometerConfig(sensorDelay ?: DEFAULT.sensorDelay)
    }
}
