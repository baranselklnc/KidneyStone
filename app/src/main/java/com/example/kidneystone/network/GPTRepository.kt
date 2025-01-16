package com.example.kidneystone.network

class GPTRepository {
    suspend fun getGPTResponse(prompt: String): String {
        val request = GPTRequest(prompt = prompt)
        val response = GPTApiClient.instance.getGPTResponse(request)
        return response.choices.first().text
    }
}
