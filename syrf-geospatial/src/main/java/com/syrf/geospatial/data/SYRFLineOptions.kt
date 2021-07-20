package com.syrf.geospatial.data

import kotlinx.serialization.Serializable

@Serializable
data class SYRFLineOptions(val npoints: Int, val offset: Int) {
    companion object {
        val DEFAULT = SYRFLineOptions(npoints = 100, offset = 10)
    }
}