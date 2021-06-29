package com.syrf.location.utils

import android.location.Location

typealias CurrentPositionUpdateCallback = (Location?, Throwable?) -> Unit

typealias SubscribeToLocationUpdateCallback = (Unit?, Throwable?) -> Unit
