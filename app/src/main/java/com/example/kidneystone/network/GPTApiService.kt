package com.example.kidneystone.network

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GPTApiService {
    // API çağrısı: POST isteği, içerik türü ve Authorization header'ı ile
    @Headers("Content-Type: application/json", "Authorization: Bearer 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")
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
