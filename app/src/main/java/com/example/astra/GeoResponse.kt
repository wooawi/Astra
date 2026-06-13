package com.example.astra.model


data class GeoResponse(
    val results: List<CityResult>
)


data class CityResult(
    val name: String,
    val country: String,
    val state: String?,
    val lat: Double,
    val lng: Double,
    val timezone: String
)