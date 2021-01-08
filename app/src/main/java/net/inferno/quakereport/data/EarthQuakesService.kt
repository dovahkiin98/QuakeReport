package net.inferno.quakereport.data

import net.inferno.quakereport.model.EarthQuakesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface EarthQuakesService {

    @GET("query?format=geojson&limit=21")
    suspend fun getEarthQuakes(
        @Query("orderby") orderby: String,
        @Query("minmag") minmag: Float,
        @Query("starttime") startdate: String,
        @Query("offset") offset: Int?,
    ): EarthQuakesResponse

    @GET("query?format=geojson&maxradiuskm=50&limit=21")
    suspend fun getEarthQuakes(
        @Query("orderby") orderby: String,
        @Query("minmag") minmag: Float,
        @Query("starttime") startdate: String,
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("offset") offset: Int?,
    ): EarthQuakesResponse
}