package com.syrf.geospatial.interfaces

import android.app.Activity
import com.syrf.geospatial.configs.CoreLibrary
import com.syrf.geospatial.configs.SYRFGeospatialConfig
import com.syrf.geospatial.data.SYRFPoint
import com.syrf.geospatial.managers.SYRFGeosManager
import com.syrf.geospatial.managers.SYRFManager
import com.syrf.geospatial.managers.SYRFTurfManager
import com.syrf.location.utils.SDKValidator

interface SYRFGeometryInterface {
    fun getPoint(latitude: Double, longitude: Double): SYRFPoint
}

/**
 * The interface that exported to the client.
 * You can use methods for working with Geospatial.
 */
interface SYRFGeospatialInterface : SYRFGeometryInterface {
    fun configure(context: Activity)
    fun configure(config: SYRFGeospatialConfig, context: Activity)
}

/**
 * The singleton, implementation of [SYRFGeospatialInterface] class.
 * This will load a native library and used it for geospatial handle
 */
object SYRFGeospatial : SYRFGeospatialInterface {

    private lateinit var config: SYRFGeospatialConfig
    private lateinit var manager: SYRFManager

    /**
     * Configure the module. The method should be called before any class usage
     * @param context The context. Should be the activity
     */
    override fun configure(context: Activity) {
        this.configure(SYRFGeospatialConfig.DEFAULT, context)
    }

    /**
     * Configure the module. The method should be called before any class usage
     * @param context The context. Should be the activity
     */
    override fun configure(config: SYRFGeospatialConfig, context: Activity) {
        SDKValidator.checkForApiKey(context)
        manager = when (config.coreLibrary) {
            CoreLibrary.GEO -> SYRFGeosManager
            CoreLibrary.TURF -> SYRFTurfManager
        }
        manager.initialize(context)
        this.config = config
    }

    /**
     * Geospacial helper method for obtaining a point object from a coordinate
     * @param latitude: The latitude coordinate of the point
     * @param longitude: The longitude coordinate of the point
     */
    override fun getPoint(latitude: Double, longitude: Double): SYRFPoint {
        return manager.getPoint(latitude, longitude)
    }

}