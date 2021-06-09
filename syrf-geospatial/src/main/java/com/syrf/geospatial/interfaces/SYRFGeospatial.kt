package com.syrf.geospatial.interfaces

interface SYRFGeospatialInterface {
    fun configure()
}

object SYRFGeospatial: SYRFGeospatialInterface {
    init {
        System.loadLibrary("geospatial");
    }

    override fun configure() {

    }

    fun test() {
        testGeospatial();
    }
    
    private external fun testGeospatial()
}