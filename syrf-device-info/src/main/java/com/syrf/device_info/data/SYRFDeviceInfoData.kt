package com.syrf.device_info.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SYRFDeviceInfoData(
    val batteryInfo: Double,
    val osVersion: String,
    val deviceModel: String,
) : Parcelable {
    /**
     * Convert rotation data to text
     */
    fun toText(): String {
        return "(batteryInfo: $batteryInfo, osVersion: $osVersion, deviceModel: $deviceModel)"
    }
}