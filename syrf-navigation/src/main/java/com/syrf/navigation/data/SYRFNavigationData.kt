package com.syrf.navigation.data

import android.os.Parcelable
import com.syrf.device_info.data.SYRFDeviceInfoData
import com.syrf.location.data.SYRFLocationData
import com.syrf.location.data.SYRFRotationSensorData
import kotlinx.parcelize.Parcelize

@Parcelize
data class SYRFNavigationData constructor(
    val location: SYRFLocationData?,
    val sensorData: SYRFRotationSensorData?,
    val deviceInfo: SYRFDeviceInfoData?,
) : Parcelable