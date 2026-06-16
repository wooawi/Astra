package com.example.astra

data class MoonResponse(
    val phase: Phase,
    val interpretation: Interpretation?

)

data class Phase(
    val name: String,
    val illumination: Double
)

data class Interpretation(
    val body: String
)