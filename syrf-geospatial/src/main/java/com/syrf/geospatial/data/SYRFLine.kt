package com.syrf.geospatial.data

import kotlinx.serialization.Serializable

@Serializable
data class SYRFLine(val type: String, val coordinates: Array<DoubleArray>): SYRFGeometry()