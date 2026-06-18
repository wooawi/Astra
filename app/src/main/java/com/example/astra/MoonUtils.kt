package com.example.astra

fun getMoonImage(phase: String): Int = when (phase) {
    "Новолуние" -> R.drawable.new_moon
    "Растущий серп" -> R.drawable.waxing_crescent
    "Первая четверть" -> R.drawable.first_quarter
    "Растущая Луна" -> R.drawable.waxing_gibbous
    "Полнолуние" -> R.drawable.full_moon
    "Убывающая Луна" -> R.drawable.waning_gibbous
    "Последняя четверть" -> R.drawable.last_quarter
    "Стареющий серп" -> R.drawable.waning_crescent
    else -> R.drawable.full_moon
}