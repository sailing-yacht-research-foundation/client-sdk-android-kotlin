package config

import com.google.android.gms.location.LocationRequest

class SYRFLocationConfig private constructor(
        val updateInterval: Long,
        val maximumLocationAccuracy: Int
) {

    companion object {
        private const val DEFAULT_UPDATE_INTERVAL: Long = 2
        const val PRIORITY_HIGH_ACCURACY = LocationRequest.PRIORITY_HIGH_ACCURACY
        const val PRIORITY_BALANCED_POWER_ACCURACY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        const val PRIORITY_LOW_POWER = LocationRequest.PRIORITY_LOW_POWER
        const val PRIORITY_NO_POWER = LocationRequest.PRIORITY_NO_POWER

        val DEFAULT: SYRFLocationConfig = SYRFLocationConfig(DEFAULT_UPDATE_INTERVAL, PRIORITY_HIGH_ACCURACY)
    }

    data class Builder(
            var updateInterval: Long? = null,
            var maximumLocationAccuracy: Int? = null
    ) {
        fun updateInterval(updateInterval: Long) = apply { this.updateInterval = updateInterval }
        fun maximumLocationAccuracy(maximumLocationAccuracy: Int) = apply { this.maximumLocationAccuracy = maximumLocationAccuracy }
        fun set() = SYRFLocationConfig(
                updateInterval ?: DEFAULT.updateInterval,
                maximumLocationAccuracy ?: DEFAULT.maximumLocationAccuracy
        )
    }
}
