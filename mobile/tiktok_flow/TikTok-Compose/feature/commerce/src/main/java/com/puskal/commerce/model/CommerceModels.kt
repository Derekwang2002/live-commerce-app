package com.puskal.commerce.model

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
    val aiSellingPoints: List<String> = emptyList()
)

enum class ProductStatus {
    ON_SALE,
    OFF_SALE
}

data class ProductSku(
    val id: String,
    val productId: String,
    val specs: Map<String, String>,
    val price: Double,
    val stock: Int,
    val skuCode: String
)

data class CartItem(
    val id: String,
    val productId: String,
    val skuId: String,
    val product: Product,
    val sku: ProductSku,
    val quantity: Int,
    val selected: Boolean = true
)

data class Cart(
    val items: List<CartItem> = emptyList()
) {
    val selectedItems: List<CartItem> get() = items.filter { it.selected }
    val totalPrice: Double get() = selectedItems.sumOf { it.sku.price * it.quantity }
    val totalCount: Int get() = items.sumOf { it.quantity }
    val selectedCount: Int get() = selectedItems.sumOf { it.quantity }
}

data class Order(
    val id: String,
    val orderNo: String,
    val userId: String,
    val address: Address,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val discountAmount: Double = 0.0,
    val payAmount: Double,
    val status: OrderStatus,
    val payExpireAt: Long? = null,
    val paidAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
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
    val subtotal: Double
)

enum class OrderStatus {
    PENDING_PAYMENT,
    PAID,
    SHIPPING,
    DELIVERED,
    COMPLETED,
    CANCELLED
}

data class Address(
    val id: String,
    val receiverName: String,
    val phone: String,
    val province: String,
    val city: String,
    val district: String,
    val detail: String,
    val isDefault: Boolean = false
)

data class Payment(
    val id: String,
    val orderId: String,
    val paymentNo: String,
    val amount: Double,
    val status: PaymentStatus,
    val paidAt: Long? = null
)

enum class PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED
}

data class CheckoutDraft(
    val source: String,
    val items: List<OrderItem>,
    val address: Address,
    val totalAmount: Double,
    val discountAmount: Double = 0.0
) {
    val payAmount: Double get() = totalAmount - discountAmount
}
