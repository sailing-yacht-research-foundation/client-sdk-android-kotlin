package com.syrf.location.configs

import android.hardware.SensorManager

/**
 * The class help you config params of Magnetic sensor request
 * @property sensorDelay The rate of sensor request frequency. Should be one of
 * [SensorManager.SENSOR_DELAY_FASTEST],
 * [SensorManager.SENSOR_DELAY_GAME],
 * [SensorManager.SENSOR_DELAY_UI],
 * [SensorManager.SENSOR_DELAY_NORMAL]
 * @property usingForegroundService True value will let tracking service running in
 * foreground mode and report data via notification when activity unbind service
 */
class SYRFRotationConfig private constructor(
    val sensorDelay: Int,
    val usingForegroundService: Boolean,
    val enabled: Boolean = true,
) {

    companion object {
        /**
         * Provide a default config for using in cases client init the SDK
         * without config or missing some properties in config
         */
        val DEFAULT: SYRFRotationConfig = SYRFRotationConfig(
            sensorDelay = SensorManager.SENSOR_DELAY_NORMAL,
            usingForegroundService = false,
            enabled = true
        )
    }

    /**
     * Builder class that help to create an instance of [SYRFRotationConfig]
     */
    data class Builder(
        var sensorDelay: Int = DEFAULT.sensorDelay,
        var usingForegroundService: Boolean = DEFAULT.usingForegroundService
    ) {
        fun sensorDelay(sensorDelay: Int) = apply { this.sensorDelay = sensorDelay }
        fun usingForegroundService(usingForegroundService: Boolean) =
            apply { this.usingForegroundService = usingForegroundService }

        fun set() = SYRFRotationConfig(sensorDelay, usingForegroundService)
    }
}
