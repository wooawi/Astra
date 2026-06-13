package com.example.astra.network


import com.example.astra.model.VedicChartRequest
import com.example.astra.model.VedicChartResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST


interface VedicApi {


    @POST("api/v2/vedic/chart")
    suspend fun generateVedicChart(

        @Header("x-api-key")
        apiKey: String,

        @Body
        request: VedicChartRequest

    ): VedicChartResponse
}