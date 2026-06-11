package com.example.astra

import retrofit2.http.GET
import retrofit2.http.Query

interface AstroApiService {

    @GET("api/v1/moon/phase")
    suspend fun getMoonPhase(

        @Query("date")
        date: String,

        @Query("include_interpretation")
        includeInterpretation: Boolean = true

    ): MoonResponse
}