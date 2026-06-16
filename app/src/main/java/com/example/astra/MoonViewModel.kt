package com.example.astra

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MoonViewModel : ViewModel() {

    var moonPhase by mutableStateOf("")
        private set

    var moonText by mutableStateOf("")
        private set

    var illumination by mutableStateOf(0)
        private set

    fun loadMoon(dateTime: String) {

        viewModelScope.launch {

            try {

                val response =
                    AstroClient.moonApi
                        .getMoonPhase(dateTime)

                moonPhase =
                    phaseRu(response.phase.name)

                illumination =
                    (response.phase.illumination * 100)
                        .toInt()

                moonText =
                    if (!response.interpretation?.body.isNullOrBlank()) {

                        TranslatorHelper.translateToRussian(
                            response.interpretation!!.body
                        )

                    } else {

                        ""
                    }

            } catch (e: Exception) {

                moonText =
                    e.message ?: "Ошибка"
            }
        }
    }
    private fun phaseRu(name: String): String = when (name) {

        "New Moon" -> "Новолуние"

        "Waxing Crescent" -> "Растущий серп"

        "First Quarter" -> "Первая четверть"

        "Waxing Gibbous" -> "Растущая Луна"

        "Full Moon" -> "Полнолуние"

        "Waning Gibbous" -> "Убывающая Луна"

        "Last Quarter" -> "Последняя четверть"

        "Waning Crescent" -> "Стареющий серп"

        else -> name
    }
}