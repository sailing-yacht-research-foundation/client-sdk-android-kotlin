package com.syrf.geospatial.managers

import android.content.Context
import com.syrf.geospatial.data.*
import com.syrf.location.interfaces.SYRFCore
import com.syrf.location.interfaces.SYRFTimber
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object SYRFTurfManager : SYRFManager {

    val converter = Json { ignoreUnknownKeys = true }

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
        val featureObject = converter.decodeFromString<SYRFFeature<SYRFPoint>>(resultString)
        return featureObject.geometry
    }

    override fun getGreatCircle(
        pointFirst: SYRFPoint,
        pointSecond: SYRFPoint,
        options: SYRFLineOptions
    ): SYRFLine {
        val functionName = getFunctionName("greatCircle")
        val resultString =
            SYRFCore.executeJavascriptFunction(
                script,
                functionName,
                converter.encodeToString(pointFirst),
                converter.encodeToString(pointSecond),
                converter.encodeToString(options),
            )
        SYRFTimber.e(resultString)
        val featureObject = converter.decodeFromString<SYRFFeature<SYRFLine>>(resultString)
        return featureObject.geometry
    }

    override fun getMidPoint(pointFirst: SYRFPoint, pointSecond: SYRFPoint): SYRFPoint {
        val functionName = getFunctionName("midpoint")
        val resultString =
            SYRFCore.executeJavascriptFunction(
                script, functionName,
                converter.encodeToString(pointFirst),
                converter.encodeToString(pointSecond),
            )
        val featureObject = converter.decodeFromString<SYRFFeature<SYRFPoint>>(resultString)
        return featureObject.geometry
    }

    override fun getLineString(
        coordinates: Array<DoubleArray>,
        options: Map<String, String>
    ): SYRFLine {
        val functionName = getFunctionName("lineString")
        val resultString =
            SYRFCore.executeJavascriptFunction(
                script,
                functionName,
                converter.encodeToString(coordinates),
                converter.encodeToString(options)
            )
        SYRFTimber.e(resultString)
        val featureObject = converter.decodeFromString<SYRFFeature<SYRFLine>>(resultString)
        return featureObject.geometry
    }

    override fun getDistance(
        pointFirst: SYRFPoint,
        pointSecond: SYRFPoint,
        unit: SYRFGeometryUnit
    ): Double {
        val functionName = getFunctionName("distance")
        val options = mutableMapOf<String, String>()
        options["units"] = unit.value
        val resultString =
            SYRFCore.executeJavascriptFunction(
                script, functionName,
                converter.encodeToString(pointFirst),
                converter.encodeToString(pointSecond),
                converter.encodeToString(options)
            )
        return resultString.toDouble()
    }

    override fun getPointToLineDistance(
        point: SYRFPoint,
        line: SYRFLine,
        unit: SYRFGeometryUnit,
        method: SYRFGeometryMethod
    ): Double {
        val functionName = getFunctionName("pointToLineDistance")
        val options = mutableMapOf<String, String>()
        options["units"] = unit.value
        options["method"] = method.value
        val resultString =
            SYRFCore.executeJavascriptFunction(
                script, functionName,
                converter.encodeToString(point),
                converter.encodeToString(line),
                converter.encodeToString(options)
            )
        return resultString.toDouble()
    }

    override fun getLineIntersect(lineFirst: SYRFLine, lineSecond: SYRFLine): Array<SYRFPoint> {
        val functionName = getFunctionName("lineIntersect")
        val resultString =
            SYRFCore.executeJavascriptFunction(
                script,
                functionName,
                converter.encodeToString(lineFirst),
                converter.encodeToString(lineSecond)
            )
        SYRFTimber.e(resultString)
        val featureCollection =
            converter.decodeFromString<SYRFFeatureCollection<SYRFPoint>>(resultString)
        return featureCollection.features.map { it.geometry }.toTypedArray()
    }

    override fun simplify(geometry: SYRFGeometry, options: SYRFSimplifyOptions): SYRFGeometry {
        return when (geometry) {
            is SYRFLine -> simplifyLine(geometry, options)
            is SYRFPoint -> simplifyPoint(geometry, options)
            else -> throw Exception("not supported geometry")
        }
    }

    private fun simplifyLine(line: SYRFLine, options: SYRFSimplifyOptions): SYRFLine {
        val functionName = getFunctionName("simplify")
        val resultString =
            SYRFCore.executeJavascriptFunction(
                script,
                functionName,
                converter.encodeToString(line),
                converter.encodeToString(options),
            )
        return converter.decodeFromString(resultString)
    }

    private fun simplifyPoint(point: SYRFPoint, options: SYRFSimplifyOptions): SYRFPoint {
        val functionName = getFunctionName("simplify")
        val resultString =
            SYRFCore.executeJavascriptFunction(
                script,
                functionName,
                converter.encodeToString(point),
                converter.encodeToString(options),
            )
        return converter.decodeFromString(resultString)
    }

    fun getFunctionName(name: String): String {
        return "Turf.Geometry.$name"
    }

}
