package com.syrf.location.configs

import android.hardware.SensorManager

/**
 * The class help you config params of Gyros sensor request
 * @property sensorDelay: rate of sensor request frequency. Should be one of
 * [SensorManager.SENSOR_DELAY_FASTEST],
 * [SensorManager.SENSOR_DELAY_GAME],
 * [SensorManager.SENSOR_DELAY_UI],
 * [SensorManager.SENSOR_DELAY_NORMAL]
 * @property usingForegroundService: if true will let tracking service running in
 * foreground mode and report data via notification when activity unbind service
 */
class SYRFGyroscopeConfig private constructor(
    val sensorDelay: Int,
    val usingForegroundService: Boolean,
) {

    companion object {
        val DEFAULT: SYRFGyroscopeConfig = SYRFGyroscopeConfig(
            sensorDelay = SensorManager.SENSOR_DELAY_NORMAL,
            usingForegroundService = false
        )
    }

    data class Builder(
        var sensorDelay: Int = DEFAULT.sensorDelay,
        var usingForegroundService: Boolean = DEFAULT.usingForegroundService
    ) {
        fun sensorDelay(sensorDelay: Int) = apply { this.sensorDelay = sensorDelay }
        fun usingForegroundService(usingForegroundService: Boolean) = apply { this.usingForegroundService = usingForegroundService }
        fun set() = SYRFGyroscopeConfig(sensorDelay, usingForegroundService)
    }
}
