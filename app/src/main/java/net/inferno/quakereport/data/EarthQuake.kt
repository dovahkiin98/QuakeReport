package net.inferno.quakereport.data

class EarthQuake(
        var mag: Double = 0.0,
        var place: String = "",
        var time: Long = 0L,
        var url: String = ""
) {
    val titleShort get() = place.substringBefore(" of") + " of"
    val placeShort get() = place.substringAfter(" of ")
}