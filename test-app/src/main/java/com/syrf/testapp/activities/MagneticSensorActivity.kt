package com.syrf.testapp.activities

import android.app.Activity
import android.content.*
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.syrf.location.configs.SYRFMagneticConfig
import com.syrf.location.data.SYRFMagneticSensorData
import com.syrf.location.interfaces.SYRFMagneticSensor
import com.syrf.location.utils.Constants.ACTION_MAGNETIC_SENSOR_BROADCAST
import com.syrf.testapp.services.TimeService
import com.syrf.time.configs.SYRFTimeConfig
import com.syrf.location.utils.Constants.EXTRA_MAGNETIC_SENSOR_DATA
import com.syrf.testapp.R
import com.syrf.testapp.databinding.ActivityMagneticSensorBinding
import com.syrf.time.interfaces.SYRFTime

class MagneticSensorActivity : AppCompatActivity() {

    private val magneticSensorBroadcastReceiver = MagneticSensorBroadcastReceiver()
    private lateinit var binding: ActivityMagneticSensorBinding
    private var isUpdateEnabled = false

    companion object {

        fun start(activity: Activity) {
            val intent = Intent(activity, MagneticSensorActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMagneticSensorBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupBtn()
    }

    override fun onStart() {
        super.onStart()

        val config = SYRFMagneticConfig.Builder()
            .sensorDelay(SensorManager.SENSOR_DELAY_NORMAL)
            .usingForegroundService(false)
            .set()
        SYRFMagneticSensor.configure(config, this)

        val timeConfig = SYRFTimeConfig.Builder()
            .set()
        SYRFTime.configure(timeConfig, this)
    }

    override fun onPause() {
        super.onPause()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            magneticSensorBroadcastReceiver
        )
    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            magneticSensorBroadcastReceiver,
            IntentFilter(
                ACTION_MAGNETIC_SENSOR_BROADCAST
            )
        )
    }

    override fun onStop() {
        SYRFMagneticSensor.onStop(this)
        super.onStop()
    }

    private fun setupBtn() {
        binding.subscribeToMagneticDataUpdateBtn.setOnClickListener() {
            isUpdateEnabled = !isUpdateEnabled
            if (isUpdateEnabled) {
                SYRFMagneticSensor.subscribeToSensorDataUpdates(this) {
                    logResultsToScreen("Device has no magnetic sensor")
                }
            } else {
                SYRFMagneticSensor.unsubscribeToSensorDataUpdates()
            }
            updateButtonState()
        }
        updateButtonState()
    }

    private fun updateButtonState() {
        binding.subscribeToMagneticDataUpdateBtn.text = if (isUpdateEnabled) {
            getString(R.string.unsubscribe_to_magnetic_updates)
        } else {
            getString(R.string.subscribe_to_magnetic_updates)
        }
    }

    private fun logResultsToScreen(output: String) {
        val outputWithPreviousLogs = "$output\n${binding.outputTextView.text}"
        binding.outputTextView.text = outputWithPreviousLogs
    }

    private inner class MagneticSensorBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val data = intent.getParcelableExtra<SYRFMagneticSensorData>(
                EXTRA_MAGNETIC_SENSOR_DATA
            )

            if (data != null) {
                logResultsToScreen("${TimeService.currentTime()} - ${data.toText()}")
            }
        }
    }
}