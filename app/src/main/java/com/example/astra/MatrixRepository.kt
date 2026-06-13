package com.example.astra.repository

import com.example.astra.model.VedicChartRequest
import com.example.astra.model.VedicChartResponse
import com.example.astra.network.VedicApi
import com.example.astra.network.WesternApi
import kotlinx.coroutines.delay

class MatrixRepository(
    private val vedicApi: VedicApi,
    private val westernApi: WesternApi
) {

    private val apiKey =
        "f3505cf2f8e65bd9d9bbf75cbe25f0ec0caa80ad5a77bfa09e1cb779a31c9167"

    suspend fun generate(
        request: VedicChartRequest
    ): Pair<VedicChartResponse, String> {

        val chart =
            vedicApi.generateVedicChart(apiKey, request)


        delay(1500)

        val svgRequest = request.copy(format = "svg")

        var svgResponse = westernApi.generateSvgChart(apiKey, svgRequest)


        if (svgResponse.code() == 429) {
            delay(3000)
            svgResponse = westernApi.generateSvgChart(apiKey, svgRequest)
        }

        if (!svgResponse.isSuccessful) {
            throw Exception("SVG error ${svgResponse.code()}")
        }

        val svg = svgResponse.body()?.string()
            ?: throw Exception("Пустой SVG")

        return chart to svg
    }
}