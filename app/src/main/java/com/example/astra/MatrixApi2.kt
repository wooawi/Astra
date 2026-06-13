package com.example.astra.network

import com.example.astra.model.VedicChartRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface WesternApi {

    @POST("api/v1/natal/chart/")
    suspend fun generateSvgChart(
        @Header("x-api-key")
        apiKey: String,

        @Body
        request: VedicChartRequest

    ): Response<ResponseBody>
}