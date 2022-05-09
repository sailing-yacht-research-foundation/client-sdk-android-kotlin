package com.syrf.testapp.activities

import android.app.Activity
import android.content.*
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.syrf.location.configs.SYRFGyroscopeConfig
import com.syrf.location.data.SYRFGyroscopeSensorData
import com.syrf.location.interfaces.SYRFGyroscopeSensor
import com.syrf.location.utils.Constants.ACTION_GYROSCOPE_SENSOR_BROADCAST
import com.syrf.testapp.services.TimeService
import com.syrf.time.configs.SYRFTimeConfig
import com.syrf.location.utils.Constants.EXTRA_GYROSCOPE_SENSOR_DATA
import com.syrf.testapp.R
import com.syrf.testapp.databinding.ActivityGyroscopeSensorBinding
import com.syrf.time.interfaces.SYRFTime

class GyroscopeSensorActivity : AppCompatActivity() {

    private val gyroscopeSensorBroadcastReceiver = GyroscopeSensorBroadcastReceiver()
    private lateinit var binding: ActivityGyroscopeSensorBinding
    private var isUpdateEnabled = false

    companion object {

        fun start(activity: Activity) {
            val intent = Intent(activity, GyroscopeSensorActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGyroscopeSensorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupBtn()
    }

    override fun onStart() {
        super.onStart()

        val config = SYRFGyroscopeConfig.Builder()
            .sensorDelay(SensorManager.SENSOR_DELAY_NORMAL)
            .usingForegroundService(false)
            .set()
        SYRFGyroscopeSensor.configure(config, this)

        val timeConfig = SYRFTimeConfig.Builder()
            .set()
        SYRFTime.configure(timeConfig, this)
    }

    override fun onPause() {
        super.onPause()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            gyroscopeSensorBroadcastReceiver
        )
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            gyroscopeSensorBroadcastReceiver,
            IntentFilter(ACTION_GYROSCOPE_SENSOR_BROADCAST)
        )
    }

    override fun onStop() {
        SYRFGyroscopeSensor.onStop(this)
        super.onStop()
    }

    private fun setupBtn() {
        binding.subscribeToGyroscopeDataUpdateBtn.setOnClickListener {
            isUpdateEnabled = !isUpdateEnabled
            if (isUpdateEnabled) {
                SYRFGyroscopeSensor.subscribeToSensorDataUpdates(this) {
                    logResultsToScreen("Device has no gyroscope sensor")
                }
            } else {
                SYRFGyroscopeSensor.unsubscribeToSensorDataUpdates()
            }
            updateButtonState()
        }
        updateButtonState()
    }

    private fun updateButtonState() {
        binding.subscribeToGyroscopeDataUpdateBtn.text = if (isUpdateEnabled) {
            getString(R.string.unsubscribe_to_gyroscope_updates)
        } else {
            getString(R.string.subscribe_to_gyroscope_updates)
        }
    }

    private fun logResultsToScreen(output: String) {
        val outputWithPreviousLogs = "$output\n${binding.outputTextView.text}"
        binding.outputTextView.text = outputWithPreviousLogs
    }

    private inner class GyroscopeSensorBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val data = intent.getParcelableExtra<SYRFGyroscopeSensorData>(
                EXTRA_GYROSCOPE_SENSOR_DATA
            )

            if (data != null) {
                logResultsToScreen("${TimeService.currentTime()} - ${data.toText()}")
            }
        }
    }
}