package net.inferno.quakereport.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class EarthQuake(
    @Json(name = "mag")
    var mag: Double,

    @Json(name = "place")
    val place: String,

    @Json(name = "time")
    var time: Long,

    @Json(name = "url")
    var url: String,
) {
    val directionText = if (place.contains("of")) place.substringBefore(" of") + " of" else null
    val placeText = if (place.contains("of")) place.substringAfter(" of ") else place
}