package com.example.astra.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

data class GeoSearchResponse(
    val results: List<GeoCity>,
    val count: Int
)

data class GeoCity(
    val name: String,
    val country: String,
    val state: String?,
    val district: String?,
    val lat: Double,
    val lng: Double,
    val timezone: String,
    val population: Int
)

interface GeoApi {

    @GET("api/v2/geo/search")
    suspend fun searchCity(
        @Header("x-api-key") apiKey: String,
        @Query("q") query: String,
        @Query("limit") limit: Int = 5
    ): GeoSearchResponse
}