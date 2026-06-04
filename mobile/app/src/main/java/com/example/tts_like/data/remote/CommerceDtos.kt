package com.example.tts_like.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val id: String,
    val title: String,
    val description: String = "",
    val coverUrl: String = "",
    val images: List<String> = emptyList(),
    val price: Double,
    val originalPrice: Double? = null,
    val status: String = "ON_SALE",
    val salesCount: Int = 0,
    val skus: List<ProductSkuResponse> = emptyList(),
    val aiTitle: String? = null,
    val aiSellingPoints: List<String> = emptyList(),
    val aiScript: String? = null,
    val tags: List<String> = emptyList(),
    val guaranteeTags: List<String> = emptyList(),
    val couponText: String? = null,
    val lowStockThreshold: Int = 5,
)

@Serializable
data class ProductSkuResponse(
    val id: String,
    val productId: String,
    val specs: Map<String, String>,
    val price: Double,
    val stock: Int,
    val skuCode: String,
)

@Serializable
data class ProductsBatchResponse(
    val products: List<ProductResponse>,
)

@Serializable
data class CartResponse(
    val items: List<CartItemResponse> = emptyList(),
)

@Serializable
data class CartItemResponse(
    val id: String,
    val productId: String,
    val skuId: String,
    val product: ProductResponse,
    val sku: ProductSkuResponse,
    val quantity: Int,
    val selected: Boolean = true,
    val invalidReason: String? = null,
)

@Serializable
data class AddCartItemRequest(
    val skuId: String,
    val quantity: Int,
)

@Serializable
data class UpdateCartItemRequest(
    val quantity: Int,
)

@Serializable
data class CartSelectionRequest(
    val cartItemIds: List<String>,
    val selected: Boolean,
)

@Serializable
data class OrdersResponse(
    val orders: List<OrderResponse> = emptyList(),
)

@Serializable
data class OrderResponse(
    val id: String,
    val orderNo: String,
    val userId: String,
    val address: AddressResponse,
    val items: List<OrderItemResponse>,
    val totalAmount: Double,
    val discountAmount: Double = 0.0,
    val shippingAmount: Double = 0.0,
    val payAmount: Double,
    val status: String,
    val payExpireAt: Long? = null,
    val paidAt: Long? = null,
    val coupon: CouponResponse? = null,
    val createdAt: Long = System.currentTimeMillis(),
)

@Serializable
data class OrderItemResponse(
    val id: String,
    val productId: String,
    val skuId: String,
    val productTitle: String,
    val coverUrl: String = "",
    val skuSpecs: Map<String, String>,
    val price: Double,
    val quantity: Int,
    val subtotal: Double,
)

@Serializable
data class AddressResponse(
    val id: String,
    val receiverName: String,
    val phone: String,
    val province: String,
    val city: String,
    val district: String,
    val detail: String,
    val isDefault: Boolean = false,
)

@Serializable
data class CouponResponse(
    val id: String,
    val title: String,
    val threshold: Double,
    val discountAmount: Double,
    val expiresInText: String,
    val productIds: List<String> = emptyList(),
)

@Serializable
data class CreateOrderRequest(
    val items: List<CreateOrderItemRequest>? = null,
)

@Serializable
data class CreateOrderItemRequest(
    val skuId: String,
    val quantity: Int,
)

@Serializable
data class CreatePaymentRequest(
    val orderId: String,
)

@Serializable
data class PaymentResponse(
    val id: String,
    val orderId: String,
    val paymentNo: String,
    val amount: Double,
    val status: String,
    val failedReason: String? = null,
    val paidAt: Long? = null,
)
