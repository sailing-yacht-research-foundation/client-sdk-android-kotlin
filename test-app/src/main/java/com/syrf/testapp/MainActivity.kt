package com.syrf.testapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.syrf.geospatial.managers.TURFManager
import com.syrf.location.interfaces.SYRFCore
import com.syrf.testapp.activities.*
import com.syrf.testapp.activities.AcceleroSensorActivity
import com.syrf.testapp.activities.GyroscopeSensorActivity
import com.syrf.testapp.activities.LocationActivity
import com.syrf.testapp.activities.MagneticSensorActivity
import com.syrf.testapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupViews()

        // Test turf
        SYRFCore.configure(this)
        TURFManager(this)
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