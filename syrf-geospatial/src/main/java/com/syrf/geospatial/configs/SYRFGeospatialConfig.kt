package com.syrf.geospatial.configs

enum class CoreLibrary {
    GEO, TURF,
}

/**
 * The class help you config params for [SYRFGeospatialConfig]
 * @property coreLibrary The library will be used in native side.
 * Should be either [CoreLibrary.GEO] or [CoreLibrary.TURF]
 */
class SYRFGeospatialConfig private constructor(
    val coreLibrary: CoreLibrary,
) {
    companion object {

        /**
         * Provide a default config for using in cases client init Geospatial
         * without config or missing some properties in config
         */
        val DEFAULT: SYRFGeospatialConfig =
            SYRFGeospatialConfig(coreLibrary = CoreLibrary.TURF)
    }

    /**
     * Builder class that help to create an instance of [SYRFGeospatialConfig]
     */
    data class Builder(
        var coreLibrary: CoreLibrary? = null,
    ) {
        fun coreLibrary(coreLibrary: CoreLibrary) = apply { this.coreLibrary = coreLibrary }
        fun set() = SYRFGeospatialConfig(
            coreLibrary ?: DEFAULT.coreLibrary,
        )
    }
}
