package com.syrf.geospatial.managers

import android.content.Context
import com.syrf.geospatial.data.SYRFPoint

object SYRFGeosManager : SYRFManager {


    override fun initialize(context: Context) {
        System.loadLibrary("geospatial");
    }

    override fun getPoint(latitude: Double, longitude: Double): SYRFPoint {
        TODO("Not yet implemented")
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