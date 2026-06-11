package com.example.astra

fun getZodiacSign(date: String): String {

    if (date.isBlank()) return "aries"

    val parts = date.split(".")

    if (parts.size != 3) return "aries"

    val day = parts[0].toInt()
    val month = parts[1].toInt()

    return when (month) {

        1 -> if (day <= 19) "capricorn" else "aquarius"
        2 -> if (day <= 18) "aquarius" else "pisces"
        3 -> if (day <= 20) "pisces" else "aries"
        4 -> if (day <= 19) "aries" else "taurus"
        5 -> if (day <= 20) "taurus" else "gemini"
        6 -> if (day <= 20) "gemini" else "cancer"
        7 -> if (day <= 22) "cancer" else "leo"
        8 -> if (day <= 22) "leo" else "virgo"
        9 -> if (day <= 22) "virgo" else "libra"
        10 -> if (day <= 22) "libra" else "scorpio"
        11 -> if (day <= 21) "scorpio" else "sagittarius"
        12 -> if (day <= 21) "sagittarius" else "capricorn"

        else -> "aries"
    }
}
fun zodiacIcon(sign: String): Int {

    return when(sign.lowercase()) {

        "aries" -> R.drawable.aries
        "taurus" -> R.drawable.taurus
        "gemini" -> R.drawable.gemini
        "cancer" -> R.drawable.cancer
        "leo" -> R.drawable.leo
        "virgo" -> R.drawable.virgo
        "libra" -> R.drawable.libra
        "scorpio" -> R.drawable.scorpio
        "sagittarius" -> R.drawable.sagittarius
        "capricorn" -> R.drawable.capricorn
        "aquarius" -> R.drawable.aquarius

        else -> R.drawable.pisces
    }
}