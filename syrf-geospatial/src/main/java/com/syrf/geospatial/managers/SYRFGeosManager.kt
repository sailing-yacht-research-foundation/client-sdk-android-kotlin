package com.syrf.geospatial.managers

import android.content.Context
import com.syrf.geospatial.data.*

object SYRFGeosManager : SYRFManager {

    override fun initialize(context: Context) {
        System.loadLibrary("geospatial");
    }

    override fun getPoint(latitude: Double, longitude: Double): SYRFPoint {
        TODO("Not yet implemented")
    }

    override fun getGreatCircle(
        pointFirst: SYRFPoint,
        pointSecond: SYRFPoint,
        options: SYRFLineOptions
    ): SYRFLine {
        TODO("Not yet implemented")
    }

    override fun getMidPoint(pointFirst: SYRFPoint, pointSecond: SYRFPoint): SYRFPoint {
        TODO("Not yet implemented")
    }

    override fun getLineString(
        coordinates: Array<DoubleArray>,
        options: Map<String, String>
    ): SYRFLine {
        TODO("Not yet implemented")
    }

    override fun getDistance(
        pointFirst: SYRFPoint,
        pointSecond: SYRFPoint,
        unit: SYRFGeometryUnit
    ): Double {
        TODO("Not yet implemented")
    }

    override fun getPointToLineDistance(
        point: SYRFPoint,
        line: SYRFLine,
        unit: SYRFGeometryUnit,
        method: SYRFGeometryMethod
    ): Double {
        TODO("Not yet implemented")
    }

    override fun getLineIntersect(lineFirst: SYRFLine, lineSecond: SYRFLine): Array<SYRFPoint> {
        TODO("Not yet implemented")
    }

    override fun simplify(geometry: SYRFGeometry, options: SYRFSimplifyOptions): SYRFGeometry {
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