package com.example.kidneystone.network

data class ProductResponse(
    val status: Int,
    val product: Product?
)

data class Product(
    val product_name: String?,
    val ingredients_text: String?,
    val nutriments: Nutriments?, // Nutriments verisi
    val categories: String?, // Kategoriler
    val categories_tags: List<String>? // Kategori etiketleri
)

data class Nutriments(
    val sodium_100g: Double?,
    val calcium_100g: Double?
)
