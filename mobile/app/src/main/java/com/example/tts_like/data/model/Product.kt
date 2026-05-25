package com.example.tts_like.data.model

data class Product(
    val id: String,
    val title: String,
    val description: String = "",
    val coverUrl: String = "",
    val images: List<String> = emptyList(),
    val price: Double,
    val originalPrice: Double? = null,
    val status: ProductStatus = ProductStatus.ON_SALE,
    val salesCount: Int = 0,
    val skus: List<ProductSku> = emptyList(),
    val aiTitle: String? = null,
    val aiSellingPoints: List<String> = emptyList(),
    val aiScript: String? = null,
)

enum class ProductStatus { ON_SALE, OFF_SALE }

data class ProductSku(
    val id: String,
    val productId: String,
    val specs: Map<String, String>,
    val price: Double,
    val stock: Int,
    val skuCode: String,
)