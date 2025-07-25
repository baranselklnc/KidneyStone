package com.example.kidneystone.network

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GPTApiService {
    // API çağrısı: POST isteği, içerik türü ile
    @Headers("Content-Type: application/json")
    @POST("v1/completions")
    suspend fun getGPTResponse(@Body request: GPTRequest): GPTResponse
}

// GPT API'sine gönderilecek istek yapısı
data class GPTRequest(
    val model: String = "text-davinci-003", // Kullanılacak model
    val prompt: String, // API'ye gönderilecek metin
    val max_tokens: Int = 10 // Döndürülecek maksimum token sayısı
)

// API'den gelen cevabın yapısı
data class GPTResponse(
    val choices: List<Choice> // Dönüş yapılacak seçenekler (metin)
)

// Her bir seçenek (response içindeki metin)
data class Choice(
    val text: String // API'den dönen metin
)
