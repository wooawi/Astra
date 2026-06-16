package com.example.astra

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await

object TranslatorHelper {

    suspend fun translateToRussian(text: String): String {

        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.RUSSIAN)
            .build()

        val translator = Translation.getClient(options)

        translator.downloadModelIfNeeded().await()

        return translator.translate(text).await()
    }
}