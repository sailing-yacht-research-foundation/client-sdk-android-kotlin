package com.syrf.geospatial.data

import kotlinx.serialization.Serializable

@Serializable
data class SYRFFeature<Geometry>(val type: String, val geometry: Geometry)