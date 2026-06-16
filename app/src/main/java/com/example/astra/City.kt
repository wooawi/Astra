package com.example.astra.model

data class City(
    val name: String,
    val asciiName: String,
    val country: String,
    val lat: Double,
    val lng: Double,
    val timezone: String,
    val population: Int
)