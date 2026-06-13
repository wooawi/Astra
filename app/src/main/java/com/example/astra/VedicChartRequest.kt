package com.example.astra.model

data class VedicChartRequest(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val city: String? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val tz_str: String? = null,
    val ayanamsha: String = "lahiri",
    val house_system: String = "whole_sign",
    val node_type: String = "mean",
    val format: String = "png",
    val theme_type: String = "dark"
)