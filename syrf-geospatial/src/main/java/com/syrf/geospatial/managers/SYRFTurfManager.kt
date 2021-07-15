package com.syrf.geospatial.managers

import android.content.Context
import com.syrf.geospatial.data.SYRFFeature
import com.syrf.geospatial.data.SYRFPoint
import com.syrf.location.interfaces.SYRFCore
import com.syrf.location.interfaces.SYRFTimber
import kotlinx.serialization.*
import kotlinx.serialization.json.Json

object SYRFTurfManager : SYRFManager {

    private const val FUNCTION_PREFIX = "Turf.Geometry."
    private val converter = Json { ignoreUnknownKeys = true }

    private lateinit var script: String

    override fun initialize(context: Context) {
        script = context.assets.open("Turf/dist/Turf.bundle.js")
            .bufferedReader()
            .use {
                it.readText()
            }
        SYRFTimber.e(script)
    }

    override fun getPoint(latitude: Double, longitude: Double): SYRFPoint {
        val functionName = getFunctionName("point")
        val resultString =
            SYRFCore.executeJavascriptFunction(script, functionName, latitude, longitude)
        SYRFTimber.e(resultString)
        val featureObject = converter.decodeFromString<SYRFFeature<SYRFPoint>>(resultString)
        return featureObject.geometry
    }


    private fun getFunctionName(name: String): String {
        return "$FUNCTION_PREFIX$name"
    }

}