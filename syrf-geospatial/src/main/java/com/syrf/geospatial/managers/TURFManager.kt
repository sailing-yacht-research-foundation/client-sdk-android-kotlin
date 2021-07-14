package com.syrf.geospatial.managers

import android.content.Context
import com.syrf.location.interfaces.SYRFCore
import com.syrf.location.interfaces.SYRFTimber
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

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

        val script = "var js_obj = {add: function(a,b) { return (a+b); }, setLocal: function(value) { this.local_val = value; }, getLocal: function() { return this.local_val; }};"

        val result = SYRFCore.executeJavascriptToGetObject(script, "js_obj.add(5,12)")

        SYRFTimber.e(result.toString())

    }

}