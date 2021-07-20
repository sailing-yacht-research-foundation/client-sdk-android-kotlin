package com.syrf.location.utils

import com.syrf.location.data.SYRFLocationData

typealias CurrentPositionUpdateCallback = (SYRFLocationData?, Throwable?) -> Unit

typealias SubscribeToLocationUpdateCallback = (Unit?, Throwable?) -> Unit
