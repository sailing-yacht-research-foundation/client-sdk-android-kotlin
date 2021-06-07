package com.syrf.testapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.syrf.testapp.activities.*
import com.syrf.testapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupViews()
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