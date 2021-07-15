package com.syrf.geospatial.managers

import android.content.Context
import com.syrf.geospatial.interfaces.SYRFGeometryInterface

interface SYRFManager: SYRFGeometryInterface {
    fun initialize(context: Context)
}