package com.syrf.navigation.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.syrf.location.data.SYRFLocationData
import com.syrf.location.utils.Constants
import com.syrf.navigation.interfaces.SYRFNavigation

class LocationBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        intent.getParcelableExtra<SYRFLocationData>(Constants.EXTRA_LOCATION)?.let {
            SYRFNavigation.location = it
            SYRFNavigation.processUpdate()
        }
    }
}
