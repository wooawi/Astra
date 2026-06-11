package com.example.astra

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AstroClient {

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->

            val request = chain.request()
                .newBuilder()
                .addHeader(
                    "x-api-key",
                    ApiConfig.API_KEY
                )
                .build()

            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.freeastroapi.com/")
        .client(client)
        .addConverterFactory(
            GsonConverterFactory.create()
        )
        .build()

    val moonApi: AstroApiService =
        retrofit.create(AstroApiService::class.java)

    val horoscopeApi: HoroscopeApiService =
        retrofit.create(HoroscopeApiService::class.java)
}