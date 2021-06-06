package com.syrf.testapp.services

import com.syrf.geospatial.interfaces.SYRFGeospatial

object GeospatialService {
    fun test() {
        SYRFGeospatial.configure()
        SYRFGeospatial.test()
    }
}