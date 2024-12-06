package com.example.kidneystone.network

data class ProductResponse(
    val status: Int,
    val product: Product?
)

data class Product(
    val product_name: String?, // Ürün adı
    val ingredients_text: String?, // İçindekiler metni
    val allergens: String?, // Alerjenler
    val nutriments: Nutriments? // Besin değerleri
)

data class Nutriments(
    val energy: String?,
    val fat: String?,
    val carbohydrates: String?,
    val proteins: String?
)
