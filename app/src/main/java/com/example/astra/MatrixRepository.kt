package com.example.astra.repository

import android.util.Log
import com.example.astra.model.VedicChartRequest
import com.example.astra.model.VedicChartResponse
import com.example.astra.network.VedicApi

class MatrixRepository(
    private val vedicApi: VedicApi
) {
    private val apiKey =
        "f3505cf2f8e65bd9d9bbf75cbe25f0ec0caa80ad5a77bfa09e1cb779a31c9167"

    suspend fun generate(request: VedicChartRequest): VedicChartResponse {
        Log.d("MATRIX", "Запрос Vedic API...")
        val chart = vedicApi.generateVedicChart(apiKey, request)
        Log.d("MATRIX", "Vedic OK: ${chart.ascendant.sign}")
        return chart
    }
}