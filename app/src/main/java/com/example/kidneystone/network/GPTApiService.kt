package com.example.kidneystone.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GPTApiService {
    @Headers("Content-Type: application/json", "Authorization: Bearer YOUR_API_KEY")
    @POST("v1/completions")
    suspend fun getGPTResponse(@Body request: GPTRequest): GPTResponse
}

data class GPTRequest(
    val model: String = "text-davinci-003",
    val prompt: String,
    val max_tokens: Int = 50
)

data class GPTResponse(
    val choices: List<Choice>
)

data class Choice(
    val text: String
)

object GPTApiClient {
    private const val BASE_URL = "https://api.openai.com/"

    val instance: GPTApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GPTApiService::class.java)
    }
}
