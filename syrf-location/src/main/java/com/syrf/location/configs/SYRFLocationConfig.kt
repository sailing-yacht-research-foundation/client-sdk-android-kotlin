package com.syrf.location.configs

import com.google.android.gms.location.LocationRequest

/**
 * The class help you config params of location request
 * @property updateInterval The interval for active location updates, in milliseconds
 * @property maximumLocationAccuracy The priority of the request
 * @property permissionRequestConfig The config for request permission dialog
 */
class SYRFLocationConfig private constructor(
    val updateInterval: Long,
    val maximumLocationAccuracy: Int,
    val permissionRequestConfig: SYRFPermissionRequestConfig? = null
) {

    companion object {
        private const val DEFAULT_UPDATE_INTERVAL: Long = 2
        const val PRIORITY_HIGH_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY
        const val PRIORITY_BALANCED_POWER_ACCURACY =
            LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        const val PRIORITY_LOW_POWER = LocationRequest.PRIORITY_LOW_POWER
        const val PRIORITY_NO_POWER = LocationRequest.PRIORITY_NO_POWER

        /**
         * Provide a default config for using in cases client init the SDK
         * without config or missing some properties in config
         */
        val DEFAULT: SYRFLocationConfig =
            SYRFLocationConfig(
                DEFAULT_UPDATE_INTERVAL,
                PRIORITY_HIGH_ACCURACY,
            )
    }

    /**
     * Builder class that help to create an instance of [SYRFLocationConfig]
     */
    data class Builder(
        var updateInterval: Long? = null,
        var maximumLocationAccuracy: Int? = null,
        var permissionRequestConfig: SYRFPermissionRequestConfig? = null
    ) {
        fun updateInterval(updateInterval: Long) = apply { this.updateInterval = updateInterval }
        fun maximumLocationAccuracy(maximumLocationAccuracy: Int) =
            apply { this.maximumLocationAccuracy = maximumLocationAccuracy }

        fun permissionRequestConfig(permissionRequestConfig: SYRFPermissionRequestConfig) =
            apply { this.permissionRequestConfig = permissionRequestConfig }

        fun set() = SYRFLocationConfig(
            updateInterval ?: DEFAULT.updateInterval,
            maximumLocationAccuracy ?: DEFAULT.maximumLocationAccuracy,
            permissionRequestConfig
        )
    }
}
