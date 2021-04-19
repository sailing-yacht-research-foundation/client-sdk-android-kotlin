package config

class SYRFLocationConfig private constructor(
        val updateInterval: Number?,
        val maximumLocationAccuracy: Number?
) {
    data class Builder(
            var updateInterval: Number? = null,
            var maximumLocationAccuracy: Number? = null
    ) {
        fun updateInterval(updateInterval: Number) = apply { this.updateInterval = updateInterval }
        fun maximumLocationAccuracy(maximumLocationAccuracy: Number) = apply { this.maximumLocationAccuracy = maximumLocationAccuracy }
        fun set() = SYRFLocationConfig(updateInterval, maximumLocationAccuracy)
    }
}
