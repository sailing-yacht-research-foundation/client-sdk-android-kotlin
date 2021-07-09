package com.syrf.testapp.activities

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.syrf.location.configs.SYRFAccelerometerConfig
import com.syrf.location.configs.SYRFMagneticConfig
import com.syrf.location.data.SYRFAcceleroSensorData
import com.syrf.location.data.SYRFMagneticSensorData
import com.syrf.location.interfaces.SYRFAcceleroSensor
import com.syrf.location.interfaces.SYRFMagneticSensor
import com.syrf.location.utils.Constants.ACTION_ACCELERO_SENSOR_BROADCAST
import com.syrf.location.utils.Constants.ACTION_MAGNETIC_SENSOR_BROADCAST
import com.syrf.location.utils.Constants.EXTRA_ACCELERO_SENSOR_DATA
import com.syrf.location.utils.Constants.EXTRA_MAGNETIC_SENSOR_DATA
import com.syrf.testapp.R
import com.syrf.testapp.databinding.ActivityFirstSampleBinding
import com.syrf.time.configs.SYRFTimeConfig
import com.syrf.time.interfaces.SYRFTime
import kotlin.math.abs

/**
 * This is a sample that using data provided by [SYRFMagneticSensor] and [SYRFAcceleroSensor]
 * to determine device's tilt and direction. This will show the tilt base on Pitch and Roll values
 * and direction based on Azimuth value
 */
class FirstSampleActivity : AppCompatActivity() {

    private val sensorBroadcastReceiver = FirstExampleBroadcastReceiver()
    private lateinit var binding: ActivityFirstSampleBinding
    private var isUpdateEnabled = false

    private var magneticSensorData: SYRFMagneticSensorData? = null
    private var acceleroSensorData: SYRFAcceleroSensorData? = null

    private var lastUpdatedTime: Long = 0
    private var currentAzimuthDegree: Float = 0f

    companion object {

        private const val TAG = "FirstSampleActivity"

        private const val VALUE_DRIFT = 0.05f

        private const val UPDATE_TIME = 100L

        fun start(activity: Activity) {
            val intent = Intent(activity, FirstSampleActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirstSampleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupBtn()
    }

    override fun onStart() {
        super.onStart()

        val magneticConfig = SYRFMagneticConfig.Builder()
            .sensorDelay(SensorManager.SENSOR_DELAY_NORMAL)
            .usingForegroundService(false)
            .set()
        SYRFMagneticSensor.configure(magneticConfig, this)

        val accelerometerConfig = SYRFAccelerometerConfig.Builder()
            .sensorDelay(SensorManager.SENSOR_DELAY_NORMAL)
            .set()
        SYRFAcceleroSensor.configure(accelerometerConfig, this)

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
        intentFilter.addAction(ACTION_ACCELERO_SENSOR_BROADCAST)
        intentFilter.addAction(ACTION_MAGNETIC_SENSOR_BROADCAST)
        LocalBroadcastManager.getInstance(this).registerReceiver(
            sensorBroadcastReceiver,
            intentFilter
        )
    }

    override fun onStop() {
        SYRFMagneticSensor.onStop(this)
        super.onStop()
    }

    private fun setupBtn() {
        binding.btnToggleSubscribe.setOnClickListener() {
            isUpdateEnabled = !isUpdateEnabled
            if (isUpdateEnabled) {
                SYRFMagneticSensor.subscribeToSensorDataUpdates(this) {
                    Log.e(TAG, "Device has no magnetic sensor")
                }
                SYRFAcceleroSensor.subscribeToSensorDataUpdates(this) {
                    Log.e(TAG, "Device has no accelerometer sensor")
                }
            } else {
                SYRFMagneticSensor.unsubscribeToSensorDataUpdates()
                SYRFAcceleroSensor.unsubscribeToSensorDataUpdates()
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

    private fun updateResult() {
        val currentTime = SYRFTime.getCurrentTimeMS()
        if (currentTime - lastUpdatedTime < UPDATE_TIME) {
            return
        }

        lastUpdatedTime = currentTime
        val orientationValues = calculateOrientations()

        updateCompass(azimuth = orientationValues[0])
        updateSpots(pitch = orientationValues[1], roll = orientationValues[2])
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

    private fun calculateOrientations(): FloatArray {
        val orientationValues = FloatArray(3)

        val magneticValues = magneticSensorData?.values ?: FloatArray(3)
        val acceleroValues = acceleroSensorData?.values ?: FloatArray(3)

        // Compute the rotation matrix: merges and translates the data
        // from the accelerometer and magnetometer, in the device coordinate
        // system, into a matrix in the world's coordinate system.
        //
        // The second argument is an inclination matrix, which isn't
        // used in this example.
        val rotationMatrix = FloatArray(9)
        val rotationOK = SensorManager.getRotationMatrix(
            rotationMatrix, null, acceleroValues, magneticValues
        )

        if (!rotationOK) {
            return orientationValues
        }

        // Remap the matrix based on current device/activity rotation.
        var rotationMatrixAdjusted = FloatArray(9)
        // Get the display from context (for rotation).
        when (display?.rotation) {
            Surface.ROTATION_0 -> rotationMatrixAdjusted = rotationMatrix.clone()
            Surface.ROTATION_90 -> SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X,
                rotationMatrixAdjusted
            )
            Surface.ROTATION_180 -> SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y,
                rotationMatrixAdjusted
            )
            Surface.ROTATION_270 -> SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X,
                rotationMatrixAdjusted
            )
        }

        // Get the orientation of the device (azimuth, pitch, roll) based
        // on the rotation matrix. Output units are radians.
        SensorManager.getOrientation(
            rotationMatrixAdjusted,
            orientationValues
        )

        return orientationValues
    }

    private inner class FirstExampleBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_MAGNETIC_SENSOR_BROADCAST -> {
                    magneticSensorData = intent.getParcelableExtra(EXTRA_MAGNETIC_SENSOR_DATA)
                }
                ACTION_ACCELERO_SENSOR_BROADCAST -> {
                    acceleroSensorData = intent.getParcelableExtra(EXTRA_ACCELERO_SENSOR_DATA)
                }
            }

            updateResult()
        }
    }
}