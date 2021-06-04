package com.syrf.testapp.activities

import SYRFTime
import android.app.Activity
import android.content.*
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.syrf.location.configs.SYRFAccelerometerConfig
import com.syrf.location.data.SYRFSensorsAcceleroData
import com.syrf.testapp.services.TimeService
import com.syrf.location.interfaces.SYRFAcceleroSensor
import com.syrf.location.utils.Constants.ACTION_ACCELERO_SENSOR_BROADCAST
import com.syrf.time.configs.SYRFTimeConfig
import com.syrf.location.utils.Constants.ACTION_LOCATION_BROADCAST
import com.syrf.location.utils.Constants.EXTRA_ACCELERO_SENSOR_DATA
import com.syrf.testapp.R
import com.syrf.testapp.databinding.ActivityAcceleroSensorBinding

class AcceleroSensorActivity : AppCompatActivity() {

    private val acceleroSensorBroadcastReceiver = AcceleroSensorBroadcastReceiver()
    private lateinit var binding: ActivityAcceleroSensorBinding
    private var isUpdateEnabled = false

    companion object {

        fun start(activity: Activity) {
            val intent = Intent(activity, AcceleroSensorActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAcceleroSensorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupBtn()
    }

    override fun onStart() {
        super.onStart()

        val config = SYRFAccelerometerConfig.Builder()
            .sensorDelay(SensorManager.SENSOR_DELAY_NORMAL)
            .set()
        SYRFAcceleroSensor.configure(config, this)

        val timeConfig = SYRFTimeConfig.Builder()
            .set()
        SYRFTime.configure(timeConfig, this)
    }

    override fun onPause() {
        super.onPause()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            acceleroSensorBroadcastReceiver
        )
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            acceleroSensorBroadcastReceiver,
            IntentFilter(
                ACTION_ACCELERO_SENSOR_BROADCAST
            )
        )
    }

    override fun onStop() {
        SYRFAcceleroSensor.onStop(this)
        super.onStop()
    }

    private fun setupBtn() {
        binding.subscribeToAcceleroDataUpdateBtn.setOnClickListener() {
            isUpdateEnabled = !isUpdateEnabled
            if (isUpdateEnabled) {
                SYRFAcceleroSensor.subscribeToLocationUpdates(this) {
                    logResultsToScreen("Device has no accelerometer sensor")
                }
            } else {
                SYRFAcceleroSensor.unsubscribeToLocationUpdates()
            }
            updateButtonState()
        }
        updateButtonState()
    }

    private fun updateButtonState() {
        binding.subscribeToAcceleroDataUpdateBtn.text = if (isUpdateEnabled) {
            getString(R.string.unsubscribe_to_accelerometer_updates)
        } else {
            getString(R.string.subscribe_to_accelerometer_updates)
        }
    }

    private fun logResultsToScreen(output: String) {
        val outputWithPreviousLogs = "$output\n${binding.outputTextView.text}"
        binding.outputTextView.text = outputWithPreviousLogs
    }

    private inner class AcceleroSensorBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val data = intent.getParcelableExtra<SYRFSensorsAcceleroData>(
                EXTRA_ACCELERO_SENSOR_DATA
            )

            if (data != null) {
                logResultsToScreen("${TimeService.currentTime()} - ${data.toText()}")
            }
        }
    }
}