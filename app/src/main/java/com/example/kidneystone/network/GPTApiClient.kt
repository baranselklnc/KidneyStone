package com.example.kidneystone.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GPTApiClient {
    private const val BASE_URL = "https://api.openai.com/"

    // OkHttpClient ile header'ı ekleyip Retrofit client'ı oluşturuyoruz
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer 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") // API Anahtarını buraya ekleyin
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    // GPTApiService arayüzünü başlatıyoruz
    val instance: GPTApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GPTApiService::class.java)
    }
}
