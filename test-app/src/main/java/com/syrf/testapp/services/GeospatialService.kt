package com.syrf.testapp.services

import android.app.Activity
import com.syrf.geospatial.interfaces.SYRFGeospatial

object GeospatialService {
    fun test(context: Activity) {
        SYRFGeospatial.configure(context)
        SYRFGeospatial.test()
    }
}