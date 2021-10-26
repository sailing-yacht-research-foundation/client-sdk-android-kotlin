package com.syrf.testapp.activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.Display
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.syrf.location.configs.SYRFRotationConfig
import com.syrf.location.data.SYRFRotationSensorData
import com.syrf.location.data.SYRFRotationData
import com.syrf.location.interfaces.SYRFRotationSensor
import com.syrf.location.utils.Constants.ACTION_ROTATION_SENSOR_BROADCAST
import com.syrf.location.utils.Constants.EXTRA_ROTATION_SENSOR_DATA
import com.syrf.testapp.R
import com.syrf.testapp.databinding.ActivitySecondSampleBinding
import com.syrf.time.configs.SYRFTimeConfig
import com.syrf.time.interfaces.SYRFTime
import kotlin.math.abs
import android.view.Surface

/**
 * This is a sample that using data provided by [SYRFRotationSensor] and device's rotation
 * to determine device's tilt and direction. This will show the tilt base on Pitch and Roll values
 * and direction based on Azimuth value
 */
class SecondSampleActivity : AppCompatActivity() {

    private val sensorBroadcastReceiver = SecondExampleBroadcastReceiver()
    private lateinit var binding: ActivitySecondSampleBinding
    private var isUpdateEnabled = false

    private var lastUpdatedTime: Long = 0
    private var currentAzimuthDegree: Float = 0f

    private val rotationMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    companion object {

        private const val TAG = "SecondSampleActivity"

        private const val VALUE_DRIFT = 0.05f

        private const val UPDATE_TIME = 100L

        fun start(activity: Activity) {
            val intent = Intent(activity, SecondSampleActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondSampleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupBtn()
    }

    override fun onStart() {
        super.onStart()

        val rotationConfig = SYRFRotationConfig.Builder()
            .sensorDelay(SensorManager.SENSOR_DELAY_NORMAL)
            .usingForegroundService(false)
            .set()
        SYRFRotationSensor.configure(rotationConfig, this)

        val timeConfig = SYRFTimeConfig.Builder()
            .set()
        SYRFTime.configure(timeConfig, this)
    }

    override fun onPause() {
        super.onPause()

        LocalBroadcastManager.getInstance(this).unregisterReceiver(
            sensorBroadcastReceiver
        )
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_ROTATION_SENSOR_BROADCAST)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            sensorBroadcastReceiver,
            intentFilter
        )
    }

    override fun onStop() {
        SYRFRotationSensor.onStop(this)
        super.onStop()
    }

    private fun setupBtn() {
        binding.btnToggleSubscribe.setOnClickListener() {
            isUpdateEnabled = !isUpdateEnabled
            if (isUpdateEnabled) {
                SYRFRotationSensor.subscribeToSensorDataUpdates(this) {
                    Log.e(TAG, "Device has no rotation sensor")
                }
            } else {
                SYRFRotationSensor.unsubscribeToSensorDataUpdates()
            }
            updateButtonState()
        }
        updateButtonState()
    }

    private fun updateButtonState() {
        binding.btnToggleSubscribe.text = if (isUpdateEnabled) {
            getString(R.string.unsubscribe)
        } else {
            getString(R.string.subscribe)
        }
    }

    private fun updateResult(data: FloatArray) {
        val currentTime = SYRFTime.getCurrentTimeMS()
        if (currentTime - lastUpdatedTime < UPDATE_TIME) {
            return
        }

        lastUpdatedTime = currentTime

        val orientationValues = calculateOrientations(data)

        updateCompass(azimuth = orientationValues.azimuth)
        updateSpots(pitch = orientationValues.pitch, roll = orientationValues.roll)
    }

    /**
     * This function will rotate compass image based on device direction
     * @param azimuth: The direction (north/south/east/west) the device is pointing.
     * 0 is magnetic north.
     */
    private fun updateCompass(azimuth: Float) {
        // Calculate azimuth in degrees
        val azimuthInDegree = Math.toDegrees(azimuth.toDouble()).toFloat()

        // Using rotate animation to animate the compass
        val animation = RotateAnimation(
            currentAzimuthDegree,
            -azimuthInDegree,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        animation.duration = UPDATE_TIME
        animation.fillAfter = true

        binding.imgCompass.startAnimation(animation)
        currentAzimuthDegree = -azimuthInDegree
    }

    /**
     * This function set spots color alpha based on pitch and roll values
     * @param pitch: The top-to-bottom tilt of the device. 0 is flat.
     * @param roll: The left-to-right tilt of the device. 0 is flat.
     */
    private fun updateSpots(pitch: Float, roll: Float) {
        // Pitch and roll values that are close to but not 0 cause the
        // animation to flash a lot. Adjust pitch and roll to 0 for very
        // small values (as defined by VALUE_DRIFT).
        val roundedPitch = if (abs(pitch) > VALUE_DRIFT) pitch else 0f
        val roundedRoll = if (abs(roll) > VALUE_DRIFT) roll else 0f

        with(binding) {
            // Reset all spot values to 0. Without this animation artifacts can
            // happen with fast tilts.
            imgSpotTop.alpha = 0f
            imgSpotBottom.alpha = 0f
            imgSpotLeft.alpha = 0f
            imgSpotRight.alpha = 0f

            // Set spot color (alpha/opacity) equal to pitch/roll.
            // this is not a precise grade (pitch/roll can be greater than 1)
            // but it's close enough for the animation effect.
            if (roundedPitch > 0) {
                imgSpotBottom.alpha = roundedPitch
            } else {
                imgSpotTop.alpha = abs(roundedPitch)
            }
            if (roundedRoll > 0) {
                imgSpotLeft.alpha = roundedRoll
            } else {
                imgSpotRight.alpha = abs(roundedRoll)
            }
        }
    }

    private fun activityDisplay() : Display? {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            return this.display
        }
        @Suppress("DEPRECATION")
        return this.windowManager.defaultDisplay
    }

    private fun calculateOrientations(rotationValues: FloatArray): SYRFRotationData {
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationValues)
        val (matrixColumn, sense) = when (val rotation =
            activityDisplay()?.rotation
        ) {
            Surface.ROTATION_0 -> Pair(0, 1)
            Surface.ROTATION_90 -> Pair(1, -1)
            Surface.ROTATION_180 -> Pair(0, -1)
            Surface.ROTATION_270 -> Pair(1, 1)
            else -> error("Invalid screen rotation value: $rotation")
        }
        val x = sense * rotationMatrix[matrixColumn]
        val y = sense * rotationMatrix[matrixColumn + 3]
        val azimuth = (-kotlin.math.atan2(y.toDouble(), x.toDouble()))

        SensorManager.getOrientation(rotationMatrix, orientation)

        val sensorRotationData = SYRFRotationData(
            azimuth = azimuth.toFloat(),
            pitch = orientation[1],
            roll = orientation[2],
            timestamp = System.currentTimeMillis())

        return sensorRotationData
    }

    private inner class SecondExampleBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            var rotationSensorData: FloatArray? = null
            when (intent.action) {
                ACTION_ROTATION_SENSOR_BROADCAST -> {
                    val data: SYRFRotationSensorData? =
                        intent.getParcelableExtra(EXTRA_ROTATION_SENSOR_DATA)
                    rotationSensorData =
                        if (data != null) floatArrayOf(data.x, data.y, data.z, data.s) else {
                            null
                        }
                }
            }

            if (rotationSensorData != null) {
                updateResult(rotationSensorData)
            }
        }
    }
}