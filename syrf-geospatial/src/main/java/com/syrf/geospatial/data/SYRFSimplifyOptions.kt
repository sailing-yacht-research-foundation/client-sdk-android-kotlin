package com.syrf.geospatial.data

import kotlinx.serialization.Serializable

@Serializable
data class SYRFSimplifyOptions(val tolerance: Int, val highQuality: Boolean, val mutate: Boolean) {
    companion object {
        val DEFAULT = SYRFSimplifyOptions(tolerance = 1, highQuality = false, mutate = false)
    }
}