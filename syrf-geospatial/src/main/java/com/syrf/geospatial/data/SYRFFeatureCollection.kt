package com.syrf.geospatial.data

import kotlinx.serialization.Serializable

@Serializable
data class SYRFFeatureCollection<Geometry>(
    val type: String,
    val features: Array<SYRFFeature<Geometry>>
)