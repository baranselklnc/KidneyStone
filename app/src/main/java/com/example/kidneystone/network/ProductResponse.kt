package com.example.kidneystone.network

data class ProductResponse(
    val status: Int,
    val product: Product?
)

data class Product(
    val product_name: String?,
    val ingredients_text: String?
)