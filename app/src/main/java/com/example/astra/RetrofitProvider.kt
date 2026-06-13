package com.example.astra.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitProvider {

    private const val BASE_URL =
        "https://api.freeastroapi.com/"

    private val okHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

    private val retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()

    val vedicApi =
        retrofit.create(
            VedicApi::class.java
        )

    val westernApi =
        retrofit.create(
            WesternApi::class.java
        )

    val geoApi =
        retrofit.create(
            GeoApi::class.java
        )
}