package com.example.kidneystone.network

data class ProductResponse(
    val status: Int,
    val product: Product?,
    val isRisky: Boolean,

){
    fun evaluateRisk(): Boolean {
        // Kategoriye göre analiz
        if (product?.categories_tags?.contains("tomato-products") == true) {
            return true // Oksalat riski
        }

        // Alternatif kategori kontrolü
        if (product?.categories?.contains("Tomato") == true) {
            return true // Oksalat riski
        }

        return false // Risk bulunamadı
    }

}

data class Product(
    val product_name: String?,
    val ingredients_text: String?,
    val nutriments: Nutriments?, // Eklenen alan
    val categories: String?, // Kategorileri alır
    val categories_tags: List<String>? // Kategori etiketlerini alır
)

data class Nutriments(
    val sodium: Double?, // mg cinsinden
    val calcium: Double? // mg cinsinden
)
