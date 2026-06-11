package com.example.astra

data class HoroscopeResponse(
    val data: HoroscopeData
)

data class HoroscopeData(
    val sign: String,
    val date: String,
    val scores: Scores,
    val content: Content,
    val astro: Astro
)

data class Scores(
    val overall: Int,
    val love: Int,
    val career: Int,
    val money: Int,
    val health: Int
)

data class Content(
    val text: String,
    val theme: String,
    val keywords: List<String>
)

data class Astro(
    val moon_phase: MoonPhase
)

data class MoonPhase(
    val key: String,
    val label: String
)