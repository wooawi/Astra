package com.example.astra

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.astra.data.CityRepository
import com.example.astra.model.Ascendant
import com.example.astra.model.City
import com.example.astra.model.Planet
import com.example.astra.model.VedicChartRequest
import com.example.astra.network.RetrofitProvider
import com.example.astra.repository.MatrixRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MatrixViewModel(application: Application) : AndroidViewModel(application) {

    private val allCities: List<City> by lazy {
        CityRepository.loadCities(getApplication())
    }

    private val repository = MatrixRepository(RetrofitProvider.vedicApi)

    var description by mutableStateOf("")
        private set

    var loading by mutableStateOf(false)
        private set

    var ascendant by mutableStateOf<Ascendant?>(null)
        private set

    var planets by mutableStateOf<List<Planet>>(emptyList())
        private set

    var citySuggestions by mutableStateOf<List<City>>(emptyList())
        private set

    private var searchJob: Job? = null

    fun searchCity(query: String) {
        searchJob?.cancel()

        if (query.length < 2) {
            citySuggestions = emptyList()
            return
        }

        searchJob = viewModelScope.launch {
            delay(250)

            val q = query.trim().lowercase()

            val startsWith = allCities
                .asSequence()
                .filter { it.name.lowercase().startsWith(q) || it.asciiName.lowercase().startsWith(q) }
                .sortedByDescending { it.population }
                .take(10)
                .toList()

            citySuggestions = startsWith.ifEmpty {
                allCities
                    .asSequence()
                    .filter { it.name.lowercase().contains(q) || it.asciiName.lowercase().contains(q) }
                    .sortedByDescending { it.population }
                    .take(10)
                    .toList()
            }
        }
    }

    fun clearCitySuggestions() {
        citySuggestions = emptyList()
    }

    fun load(date: String, time: String, city: City) {
        viewModelScope.launch(Dispatchers.IO) {
            loading = true
            try {
                val d = date.split(".")
                val t = time.split(":")
                if (d.size != 3 || t.size != 2) throw Exception("Неверная дата или время")

                val request = VedicChartRequest(
                    year = d[2].toInt(),
                    month = d[1].toInt(),
                    day = d[0].toInt(),
                    hour = t[0].toInt(),
                    minute = t[1].toInt(),
                    city = city.name,
                    lat = city.lat,
                    lng = city.lng,
                    tz_str = city.timezone
                )

                val result = repository.generate(request)

                ascendant = result.ascendant
                planets = result.planets
                description = buildString {
                    appendLine("Асцендент: ${signRu(result.ascendant.sign)}")
                    appendLine()
                    result.planets.forEach {
                        appendLine("${planetRu(it.name)} — ${signRu(it.sign)}, дом ${it.house}")
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