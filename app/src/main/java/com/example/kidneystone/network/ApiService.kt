package com.example.kidneystone.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

object ApiService {
    private const val BASE_URL = "https://world.openfoodfacts.org/api/v0/product/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: OpenFoodFactsApi = retrofit.create(OpenFoodFactsApi::class.java)
}

interface OpenFoodFactsApi {
    @GET("{barcode}.json")
    suspend fun getProductInfo(@Path("barcode") barcode: String): ProductResponse
}
