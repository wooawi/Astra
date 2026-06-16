package com.example.astra

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.astra.model.Ascendant
import com.example.astra.model.Planet
import com.example.astra.model.VedicChartRequest
import com.example.astra.network.RetrofitProvider
import com.example.astra.repository.MatrixRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MatrixViewModel : ViewModel() {

    private val repository = MatrixRepository(RetrofitProvider.vedicApi)

    private val apiKey =
        "f3505cf2f8e65bd9d9bbf75cbe25f0ec0caa80ad5a77bfa09e1cb779a31c9167"

    var description by mutableStateOf("")
        private set

    var loading by mutableStateOf(false)
        private set

    var ascendant by mutableStateOf<Ascendant?>(null)
        private set

    var planets by mutableStateOf<List<Planet>>(emptyList())
        private set

    var citySuggestions by mutableStateOf<List<String>>(emptyList())

    private suspend fun findCity(city: String): Triple<Double, Double, String> {
        val result = RetrofitProvider.geoApi.searchCity(apiKey, city)
        if (result.results.isEmpty()) throw Exception("Город не найден: $city")
        val item = result.results.first()
        return Triple(item.lat, item.lng, item.timezone)
    }

    fun searchCity(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (query.length < 2) {
                    citySuggestions = emptyList()
                    return@launch
                }
                val result = RetrofitProvider.geoApi.searchCity(apiKey, query)
                citySuggestions = result.results.map { it.name }
            } catch (e: Exception) {
                citySuggestions = emptyList()
            }
        }
    }

    fun clearCitySuggestions() {
        citySuggestions = emptyList()
    }

    fun load(date: String, time: String, city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loading = true
            try {
                val d = date.split(".")
                val t = time.split(":")
                if (d.size != 3 || t.size != 2) throw Exception("Неверная дата или время")

                val location = findCity(city)

                val request = VedicChartRequest(
                    year = d[2].toInt(),
                    month = d[1].toInt(),
                    day = d[0].toInt(),
                    hour = t[0].toInt(),
                    minute = t[1].toInt(),
                    city = city,
                    lat = location.first,
                    lng = location.second,
                    tz_str = location.third
                )

                val result = repository.generate(request)

                ascendant = result.ascendant
                planets = result.planets
                description = buildString {

                    appendLine("Асцендент: ${signRu(result.ascendant.sign)}")
                    appendLine()

                    result.planets.forEach {

                        appendLine(
                            "${planetRu(it.name)} — ${signRu(it.sign)}, дом ${it.house}"
                        )
                    }
                }

            } catch (e: Exception) {
                Log.e("MATRIX", "ОШИБКА: ${e.message}", e)
                description = e.message ?: "Ошибка"
            }
            loading = false
        }

    }
    private fun planetRu(name: String): String = when (name) {
        "Sun" -> "Солнце"
        "Moon" -> "Луна"
        "Mars" -> "Марс"
        "Mercury" -> "Меркурий"
        "Jupiter" -> "Юпитер"
        "Venus" -> "Венера"
        "Saturn" -> "Сатурн"
        "Rahu" -> "Раху"
        "Ketu" -> "Кету"
        "Uranus" -> "Уран"
        "Neptune" -> "Нептун"
        "Pluto" -> "Плутон"
        else -> name
    }

    private fun signRu(sign: String): String = when (sign) {
        "Aries" -> "Овен"
        "Taurus" -> "Телец"
        "Gemini" -> "Близнецы"
        "Cancer" -> "Рак"
        "Leo" -> "Лев"
        "Virgo" -> "Дева"
        "Libra" -> "Весы"
        "Scorpio" -> "Скорпион"
        "Sagittarius" -> "Стрелец"
        "Capricorn" -> "Козерог"
        "Aquarius" -> "Водолей"
        "Pisces" -> "Рыбы"
        else -> sign
    }


}