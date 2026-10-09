package com.jainpanchang.engine.model

import kotlinx.serialization.Serializable
import java.time.ZoneId

@Serializable
data class GeoLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val elevationMeters: Double = 0.0,
    val timezoneId: String
) {
    val zoneId: ZoneId get() = ZoneId.of(timezoneId)
}
