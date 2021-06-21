package com.syrf.geospatial.interfaces

/**
 * The interface that exported to the client.
 * You can use methods for working with Geospatial.
 */
interface SYRFGeospatialInterface {
    fun configure()
}

/**
 * The singleton, implementation of [SYRFGeospatialInterface] class.
 * This will load a native library and used it for geospatial handle
 */
object SYRFGeospatial: SYRFGeospatialInterface {

    /**
     * Load the native library
     */
    init {
        System.loadLibrary("geospatial");
    }

    /**
     * Configure the module. The method should be called before any class usage
     */
    override fun configure() {

    }
    /**
     * The function for testing geospatial working purpose
     */
    fun test() {
        testGeospatial();
    }

    /**
     * The link to native function from library for testing
     */
    private external fun testGeospatial()
}