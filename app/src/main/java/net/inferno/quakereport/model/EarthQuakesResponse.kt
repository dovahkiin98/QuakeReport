package net.inferno.quakereport.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class EarthQuakesResponse(
    @Json(name = "features")
    val features: List<Feature>
)

@JsonClass(generateAdapter = true)
class Feature(
    @Json(name = "properties")
    val properties: EarthQuake
)