package com.syrf.location.utils

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.location.Location

fun Location?.toText(): String {
    return if (this != null) {
        "($latitude, $longitude)"
    } else {
        "Unknown location"
    }
}

@Suppress("DEPRECATION")
fun <T> Context.serviceIsRunningInForeground(serviceClz: Class<T>): Boolean {
    val manager = getSystemService(
        Service.ACTIVITY_SERVICE
    ) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClz.name == service.service.className) {
            if (service.foreground) {
                return true
            }
        }
    }
    return false
}