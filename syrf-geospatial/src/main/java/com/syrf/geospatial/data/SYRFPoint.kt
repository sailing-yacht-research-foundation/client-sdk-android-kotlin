package com.syrf.geospatial.data

import kotlinx.serialization.Serializable

@Serializable
data class SYRFPoint(val type: String, val coordinates: DoubleArray)