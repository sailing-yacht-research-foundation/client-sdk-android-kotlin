package com.syrf.geospatial.managers

import android.content.Context
import com.syrf.core.interfaces.SYRFCore
import com.syrf.geospatial.utils.readFileDirectlyAsText
import java.io.File

class TURFManager (context: Context) {
    /**
    Initializer for JavaScript environment
    Loads turf from bundle
    Loads main object and exported methods into jsGeometry
     */

    init {
        initTurf(context)
    }

    private fun initTurf(context: Context) {

        val string = context.assets.open("Turf/dist/Turf.bundle.js").bufferedReader().use {
            it.readText()
        }
        val result = SYRFCore.executeJavascript(string)

    }

}