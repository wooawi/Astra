package com.example.astra

fun getZodiacSign(day: Int, month: Int): String {

    return when {

        (month == 3 && day >= 21) || (month == 4 && day <= 19) -> "aries"

        (month == 4 && day >= 20) || (month == 5 && day <= 20) -> "taurus"

        (month == 5 && day >= 21) || (month == 6 && day <= 20) -> "gemini"

        (month == 6 && day >= 21) || (month == 7 && day <= 22) -> "cancer"

        (month == 7 && day >= 23) || (month == 8 && day <= 22) -> "leo"

        (month == 8 && day >= 23) || (month == 9 && day <= 22) -> "virgo"

        (month == 9 && day >= 23) || (month == 10 && day <= 22) -> "libra"

        (month == 10 && day >= 23) || (month == 11 && day <= 21) -> "scorpio"

        (month == 11 && day >= 22) || (month == 12 && day <= 21) -> "sagittarius"

        (month == 12 && day >= 22) || (month == 1 && day <= 19) -> "capricorn"

        (month == 1 && day >= 20) || (month == 2 && day <= 18) -> "aquarius"

        else -> "pisces"
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