package com.syrf.geospatial.managers

import android.content.Context
import com.syrf.location.interfaces.SYRFCore
import com.syrf.location.interfaces.SYRFTimber

class TURFManager(context: Context) {

    /**
    Initializer for JavaScript environment
    Loads turf from bundle
    Loads main object and exported methods into jsGeometry
     */
    init {
        initTurf(context)
    }

    private fun initTurf(context: Context) {

        val string = context.assets.open("Turf/dist/Turf.bundle.js")
            .bufferedReader()
            .use {
                it.readText()
            }
        SYRFTimber.e(string)

        val result = SYRFCore.executeJavascriptToGetObject(string, "point({1, 2})")

        SYRFTimber.e(result.toString())

    }

}