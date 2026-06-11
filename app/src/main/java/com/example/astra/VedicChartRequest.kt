package com.example.astra.model

data class VedicChartRequest(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val city: String,
    val ayanamsha: String = "lahiri",
    val house_system: String = "whole_sign",
    val node_type: String = "mean"
)

