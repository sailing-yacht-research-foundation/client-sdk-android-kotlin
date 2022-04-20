package com.syrf.device_info.interfaces

import android.content.Context
import android.os.BatteryManager
import android.os.Build

/**
 * The interface that exported to the client.
 */
interface SYRFDeviceInfoInterface {
    fun getBatteryLevel(context: Context): Int
    fun getPhoneModel(): String
    fun getOsVersion(): String
}

/**
 * The singleton, implementation of [SYRFDeviceInfoInterface] class.
 * Return device information, battery level
 */
object SYRFDeviceInfo : SYRFDeviceInfoInterface {
    override fun getBatteryLevel(context: Context): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    override fun getPhoneModel(): String = "${Build.MANUFACTURER} - ${Build.MODEL}"

    override fun getOsVersion(): String = Build.VERSION.RELEASE
}