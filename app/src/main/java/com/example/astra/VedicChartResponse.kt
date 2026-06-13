package com.example.astra.model


data class VedicChartResponse(

    val ascendant: Ascendant,

    val planets: List<Planet>,

    val metadata: ChartMetadata?
)


data class Ascendant(

    val sign: String,

    val degree: Double
)


data class Planet(

    val name: String,

    val sign: String,

    val house: Int
)


data class ChartMetadata(

    val timezone_used: String?
)