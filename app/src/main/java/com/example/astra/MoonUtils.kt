package com.example.astra

fun getMoonImage(phase: String): Int {

    return when (phase) {

        "New Moon" -> R.drawable.new_moon

        "Waxing Crescent" -> R.drawable.waxing_crescent

        "First Quarter" -> R.drawable.first_quarter

        "Waxing Gibbous" -> R.drawable.waxing_gibbous

        "Full Moon" -> R.drawable.full_moon

        "Waning Gibbous" -> R.drawable.waning_gibbous

        "Last Quarter" -> R.drawable.last_quarter

        "Waning Crescent" -> R.drawable.waning_crescent

        else -> R.drawable.full_moon
    }
}