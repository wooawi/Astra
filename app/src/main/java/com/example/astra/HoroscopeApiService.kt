package com.example.astra

import retrofit2.http.GET
import retrofit2.http.Query

interface HoroscopeApiService {

    @GET("api/v2/horoscope/daily/sign")
    suspend fun getDailySign(
        @Query("sign") sign: String,
        @Query("date") date: String = "today",
        @Query("tz_str") tz: String = "AUTO",
        @Query("locale") locale: String = "ru"
    ): HoroscopeResponse
}