package com.syrf.navigation.data

import com.syrf.device_info.data.SYRFDeviceInfoConfig
import com.syrf.location.configs.SYRFLocationConfig
import com.syrf.location.configs.SYRFRotationConfig

data class SYRFNavigationConfig constructor(
    val locationConfig: SYRFLocationConfig? = null,
    val headingConfig: SYRFRotationConfig? = null,
    val deviceInfoConfig: SYRFDeviceInfoConfig? = null,
    val throttleForegroundDelay: Int = 1000,
    val throttleBackgroundDelay: Int = 2000,
)