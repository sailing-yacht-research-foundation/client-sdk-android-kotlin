package com.syrf.geospatial.interfaces

import android.app.Activity
import com.syrf.geospatial.configs.CoreLibrary
import com.syrf.geospatial.configs.SYRFGeospatialConfig
import com.syrf.geospatial.data.*
import com.syrf.geospatial.managers.SYRFGeosManager
import com.syrf.geospatial.managers.SYRFManager
import com.syrf.geospatial.managers.SYRFTurfManager
import com.syrf.geospatial.data.SYRFGeometryUnit
import com.syrf.location.utils.SDKValidator

interface SYRFGeometryInterface {
    fun getPoint(latitude: Double, longitude: Double): SYRFPoint
    fun getMidPoint(pointFirst: SYRFPoint, pointSecond: SYRFPoint): SYRFPoint
    fun getGreatCircle(
        pointFirst: SYRFPoint,
        pointSecond: SYRFPoint,
        options: SYRFLineOptions = SYRFLineOptions.DEFAULT
    ): SYRFLine

    fun getLineString(
        coordinates: Array<DoubleArray>,
        options: Map<String, String> = emptyMap()
    ): SYRFLine

    fun getDistance(
        pointFirst: SYRFPoint,
        pointSecond: SYRFPoint,
        unit: SYRFGeometryUnit = SYRFGeometryUnit.METERS
    ): Double

    fun getPointToLineDistance(
        point: SYRFPoint,
        line: SYRFLine,
        unit: SYRFGeometryUnit = SYRFGeometryUnit.METERS,
        method: SYRFGeometryMethod = SYRFGeometryMethod.GEODESIC
    ): Double

    fun getLineIntersect(lineFirst: SYRFLine, lineSecond: SYRFLine): Array<SYRFPoint>

    // Todo: make this method generic so it can help simplification all subclass of SYRFGeometry
    fun simplify(
        line: SYRFLine,
        options: SYRFSimplifyOptions = SYRFSimplifyOptions.DEFAULT
    ): SYRFLine
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
     * The method for obtaining a point object from a coordinate
     * @param latitude: The latitude coordinate of the point
     * @param longitude: The longitude coordinate of the point
     */
    override fun getPoint(latitude: Double, longitude: Double): SYRFPoint {
        return manager.getPoint(latitude, longitude)
    }

    /**
     * The for obtaining a great line between two points
     * @param pointFirst: The first point
     * @param pointSecond: The second point
     * @param options: The great circle additional configuration
     */
    override fun getGreatCircle(
        pointFirst: SYRFPoint,
        pointSecond: SYRFPoint,
        options: SYRFLineOptions
    ): SYRFLine {
        return manager.getGreatCircle(pointFirst, pointSecond, options)
    }

    /**
     * The method for obtaining a the middle geodesically point between two points
     * @param pointFirst: The first point
     * @param pointSecond: The second point
     */
    override fun getMidPoint(pointFirst: SYRFPoint, pointSecond: SYRFPoint): SYRFPoint {
        return manager.getMidPoint(pointFirst, pointSecond)
    }

    /**
     * The method for obtaining a line constructed from an array of points
     * @param  coordinates: The array of points
     * @param  options: The additional options for generating the line
     */
    override fun getLineString(
        coordinates: Array<DoubleArray>,
        options: Map<String, String>
    ): SYRFLine {
        return manager.getLineString(coordinates, options)
    }

    /**
     * The method for obtaining the distance between two points
     * @param pointFirst: The first point
     * @param pointSecond: The second point
     * @param unit: The unit of return value
     */
    override fun getDistance(
        pointFirst: SYRFPoint,
        pointSecond: SYRFPoint,
        unit: SYRFGeometryUnit
    ): Double {
        return manager.getDistance(pointFirst, pointSecond, unit)
    }

    /**
     * The method for obtaining the minimal distance between a point and a line
     * @param point: The point
     * @param line: The line
     * @param unit: The unit of return value
     * @param method: The method will be used for calculation
     */
    override fun getPointToLineDistance(
        point: SYRFPoint,
        line: SYRFLine,
        unit: SYRFGeometryUnit,
        method: SYRFGeometryMethod
    ): Double {
        return manager.getPointToLineDistance(point, line, unit, method)
    }

    /**
     * The method for obtaining the intersecting points of two lines
     * @param lineFirst: The first line
     * @param lineSecond: The second line
     */
    override fun getLineIntersect(lineFirst: SYRFLine, lineSecond: SYRFLine): Array<SYRFPoint> {
        return manager.getLineIntersect(lineFirst, lineSecond)
    }

    /**
     * The method for simplification the line
     * @param line: The first
     * @param options: The additional options for the simplify operation
     */
    override fun simplify(line: SYRFLine, options: SYRFSimplifyOptions): SYRFLine {
        return manager.simplify(line, options)
    }

}