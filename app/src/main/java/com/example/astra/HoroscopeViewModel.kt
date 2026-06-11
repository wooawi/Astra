package com.example.astra

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HoroscopeViewModel : ViewModel() {

    var loading by mutableStateOf(false)

    var sign by mutableStateOf("leo")

    var text by mutableStateOf("")
    var overall by mutableStateOf(0)

    fun load(signInput: String) {

        viewModelScope.launch {

            try {

                val response =
                    AstroClient.horoscopeApi
                        .getDailySign(signInput)

                sign = response.data.sign
                text = response.data.content.text
                overall = response.data.scores.overall

            } catch (e: Exception) {

                text = e.message ?: "Ошибка"
            }
        }
    }
}