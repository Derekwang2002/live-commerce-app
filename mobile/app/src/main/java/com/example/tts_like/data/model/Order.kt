package com.example.tts_like.data.model

data class Order(
    val id: String,
    val orderNo: String,
    val userId: String,
    val address: Address,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val discountAmount: Double = 0.0,
    val shippingAmount: Double = 0.0,
    val payAmount: Double,
    val status: OrderStatus,
    val payExpireAt: Long? = null,
    val paidAt: Long? = null,
    val coupon: Coupon? = null,
    val createdAt: Long = System.currentTimeMillis(),
)

data class OrderItem(
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

enum class OrderStatus {
    PENDING_PAYMENT,
    PAID,
    SHIPPING,
    DELIVERED,
    COMPLETED,
    CANCELLED,
}

data class Address(
    val id: String,
    val receiverName: String,
    val phone: String,
    val province: String,
    val city: String,
    val district: String,
    val detail: String,
    val isDefault: Boolean = false,
)

data class Payment(
    val id: String,
    val orderId: String,
    val paymentNo: String,
    val amount: Double,
    val status: PaymentStatus,
    val failedReason: String? = null,
    val paidAt: Long? = null,
)

enum class PaymentStatus { PENDING, SUCCESS, FAILED }
