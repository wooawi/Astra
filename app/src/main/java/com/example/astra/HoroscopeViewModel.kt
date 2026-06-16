package com.example.astra

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HoroscopeViewModel : ViewModel() {

    var loading by mutableStateOf(false)
        private set

    var sign by mutableStateOf("leo")
        private set

    var text by mutableStateOf("")
        private set

    var overall by mutableStateOf(0)
        private set

    fun load(signInput: String) {

        viewModelScope.launch {

            try {

                loading = true

                val response =
                    AstroClient.horoscopeApi
                        .getDailySign(signInput)

                sign = response.data.sign

                text =
                    TranslatorHelper.translateToRussian(
                        response.data.content.text
                    )

                overall = response.data.scores.overall

            } catch (e: Exception) {

                text = e.message ?: "Ошибка загрузки гороскопа"

            } finally {

                loading = false
            }
        }
    }
}