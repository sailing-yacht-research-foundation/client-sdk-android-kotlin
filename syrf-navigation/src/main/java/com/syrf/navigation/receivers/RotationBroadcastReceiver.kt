package com.syrf.navigation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.syrf.location.data.SYRFRotationSensorData
import com.syrf.location.utils.Constants
import com.syrf.navigation.interfaces.SYRFNavigation

class RotationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        intent.getParcelableExtra<SYRFRotationSensorData>(Constants.EXTRA_ROTATION_SENSOR_DATA)
            ?.let { SYRFNavigation.sensorData = it }
    }
}
