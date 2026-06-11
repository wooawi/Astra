package com.example.astra

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MoonViewModel : ViewModel() {

    var moonPhase by mutableStateOf("")
    var moonText by mutableStateOf("")
    var illumination by mutableStateOf(0)

    fun loadMoon(dateTime: String) {

        viewModelScope.launch {

            try {

                val response =
                    AstroClient.moonApi
                        .getMoonPhase(dateTime)

                moonPhase =
                    response.phase.name

                illumination =
                    (response.phase.illumination * 100)
                        .toInt()

                moonText =
                    response.interpretation?.body
                        ?: ""

            } catch (e: Exception) {

                moonText =
                    e.message ?: "Ошибка"
            }
        }
    }
}