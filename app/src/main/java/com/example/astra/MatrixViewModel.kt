package com.example.astra

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.astra.model.VedicChartRequest
import com.example.astra.network.RetrofitProvider
import com.example.astra.repository.MatrixRepository
import kotlinx.coroutines.launch

class MatrixViewModel : ViewModel() {

    private val repository =
        MatrixRepository(
            RetrofitProvider.vedicApi,
            RetrofitProvider.westernApi
        )

    private val apiKey =
        "f3505cf2f8e65bd9d9bbf75cbe25f0ec0caa80ad5a77bfa09e1cb779a31c9167"

    var description by mutableStateOf("")
        private set

    var svgChart by mutableStateOf("")
        private set

    var loading by mutableStateOf(false)
        private set

    var citySuggestions by mutableStateOf<List<String>>(emptyList())

    private suspend fun findCity(
        city: String
    ): Triple<Double, Double, String> {

        val result =
            RetrofitProvider.geoApi.searchCity(
                apiKey,
                city
            )

        if (result.results.isEmpty()) {
            throw Exception("Город не найден: $city")
        }

        val item =
            result.results.first()

        return Triple(
            item.lat,
            item.lng,
            item.timezone
        )
    }

    fun searchCity(query: String) {
        viewModelScope.launch {
            try {
                if (query.length < 2) {
                    citySuggestions = emptyList()
                    return@launch
                }

                val result = RetrofitProvider.geoApi.searchCity(
                    apiKey,
                    query
                )

                citySuggestions = result.results.map { it.name }

            } catch (e: Exception) {
                citySuggestions = emptyList()
            }
        }
    }

    fun clearCitySuggestions() {
        citySuggestions = emptyList()
    }

    fun load(
        date: String,
        time: String,
        city: String
    ) {

        viewModelScope.launch {

            loading = true

            try {

                val d =
                    date.split(".")

                val t =
                    time.split(":")

                if (d.size != 3 || t.size != 2) {
                    throw Exception(
                        "Неверная дата или время"
                    )
                }

                // получаем координаты города
                val location =
                    findCity(city)

                val lat =
                    location.first

                val lng =
                    location.second

                val timezone =
                    location.third

                val request =
                    VedicChartRequest(
                        year = d[2].toInt(),
                        month = d[1].toInt(),
                        day = d[0].toInt(),
                        hour = t[0].toInt(),
                        minute = t[1].toInt(),
                        city = city,
                        lat = lat,
                        lng = lng,
                        tz_str = timezone,
                        format = "svg",
                        theme_type = "dark"
                    )

                val result =
                    repository.generate(request)

                svgChart =
                    result.second

                val vedic =
                    result.first

                description =
                    buildString {

                        appendLine(
                            "Асцендент: ${vedic.ascendant.sign}"
                        )

                        appendLine()

                        vedic.planets.forEach {

                            appendLine(
                                "${it.name} — ${it.sign}, дом ${it.house}"
                            )

                        }

                    }

            } catch (e: Exception) {

                description =
                    e.message ?: "Ошибка"

            }

            loading = false

        }

    }

}