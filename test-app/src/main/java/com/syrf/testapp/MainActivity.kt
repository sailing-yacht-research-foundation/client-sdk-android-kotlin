package com.syrf.testapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.syrf.geospatial.data.SYRFFeature
import com.syrf.geospatial.data.SYRFLine
import com.syrf.geospatial.data.SYRFLineOptions
import com.syrf.geospatial.data.SYRFPoint
import com.syrf.geospatial.interfaces.SYRFGeospatial
import com.syrf.location.interfaces.SYRFCore
import com.syrf.location.interfaces.SYRFTimber
import com.syrf.testapp.activities.*
import com.syrf.testapp.activities.AcceleroSensorActivity
import com.syrf.testapp.activities.GyroscopeSensorActivity
import com.syrf.testapp.activities.LocationActivity
import com.syrf.testapp.activities.MagneticSensorActivity
import com.syrf.testapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupViews()

        // Test turf
        SYRFCore.configure(this)
        SYRFGeospatial.configure(this)

        // getPoint
        val pointFirst = SYRFGeospatial.getPoint(100.5, 21.2)
        val pointSecond = SYRFGeospatial.getPoint(110.5, 24.2)
        SYRFTimber.e(pointFirst.toString())
        SYRFTimber.e(pointSecond.toString())

        // getGreatCircle
        val options = SYRFLineOptions(npoints = 80, offset = 8)
        val line = SYRFGeospatial.getGreatCircle(pointFirst, pointSecond, options)
        SYRFTimber.e(line.toString())
        SYRFTimber.e(line.coordinates.size.toString())

        // getMidPoint
        val midPoint = SYRFGeospatial.getMidPoint(pointFirst, pointSecond)
        SYRFTimber.e(midPoint.toString())

        // getLineString
        val coordinates = arrayOf(
            doubleArrayOf(1.2, 4.0),
            doubleArrayOf(5.6, 10.0),
            doubleArrayOf(2.5, 6.0),
            doubleArrayOf(4.2, 7.3)
        )
        val options2 = mutableMapOf<String, String>()
        options2["name"] = "line 1"
        val line2 = SYRFGeospatial.getLineString(coordinates, options2)
        SYRFTimber.e(line2.coordinates.size.toString())

        // getDistance
        val distance = SYRFGeospatial.getDistance(pointFirst, pointSecond)
        SYRFTimber.e(distance.toString())

        // getPointToLineDistance
        val pointToLineDistance = SYRFGeospatial.getPointToLineDistance(pointFirst, line2)
        SYRFTimber.e(pointToLineDistance.toString())

        // getLineIntersect
        val lineFirst = SYRFGeospatial.getLineString(
            arrayOf(
                doubleArrayOf(1.2, 4.0),
                doubleArrayOf(5.6, 10.0),
                doubleArrayOf(2.5, 6.0),
            ),
            mapOf(Pair("name", "line 1"))
        )
        val lineSecond = SYRFGeospatial.getLineString(
            arrayOf(
                doubleArrayOf(2.5, 6.0),
                doubleArrayOf(4.2, 7.3)
            ),
            mapOf(Pair("name", "line 2"))
        )
        val intersectPoint = SYRFGeospatial.getLineIntersect(lineFirst, lineSecond)
        intersectPoint.forEach {
            SYRFTimber.e(it.toString())
        }

        // simplify
        val simplifiedLine = SYRFGeospatial.simplify(line)
        SYRFTimber.e(simplifiedLine.toString())
    }

    private fun setupViews() {
        binding.btnShowLocation.setOnClickListener {
            LocationActivity.start(this)
        }

        binding.btnShowAcceleroSensor.setOnClickListener {
            AcceleroSensorActivity.start(this)
        }

        binding.btnShowMagneticSensor.setOnClickListener {
            MagneticSensorActivity.start(this)
        }

        binding.btnShowGyroscopeSensor.setOnClickListener {
            GyroscopeSensorActivity.start(this)
        }

        binding.btnShowFirstSample.setOnClickListener {
            FirstSampleActivity.start(this)
        }
    }
}