package com.syrf.geospatial.interfaces

import android.app.Activity
import com.syrf.location.utils.NoConfigException
import com.syrf.location.utils.SDKValidator

/**
 * The interface that exported to the client.
 * You can use methods for working with Geospatial.
 */
interface SYRFGeospatialInterface {
    fun configure(context: Activity)
}

/**
 * The singleton, implementation of [SYRFGeospatialInterface] class.
 * This will load a native library and used it for geospatial handle
 */
object SYRFGeospatial : SYRFGeospatialInterface {

    private var isConfigured = false

    /**
     * Load the native library
     */
    init {
        System.loadLibrary("geospatial");
    }

    /**
     * Configure the module. The method should be called before any class usage
     * @param context The context. Should be the activity
     */
    override fun configure(context: Activity) {
        SDKValidator.checkForApiKey(context)
        isConfigured = true
    }

    /**
     * The function for testing geospatial working purpose
     * @throws NoConfigException
     */
    fun test() {
        if (!isConfigured) {
            throw NoConfigException()
        }
        testGeospatial();
    }

    /**
     * The link to native function from library for testing
     */
    private external fun testGeospatial()
}