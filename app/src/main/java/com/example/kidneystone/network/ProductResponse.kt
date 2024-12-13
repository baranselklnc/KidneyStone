package com.example.kidneystone.network

data class ProductResponse(
    val status: Int,
    val product: Product?,
    val isRisky: Boolean,

)



data class Product(
    val product_name: String?,
    val ingredients_text: String?,
    val nutriments: Nutriments?, // Eklenen alan
    val categories: String?, // Kategorileri alır
    val categories_tags: List<String>? // Kategori etiketlerini alır
)

data class Nutriments(
    val sodium_100g: Double?,
    val calcium_100g: Double?,
)
