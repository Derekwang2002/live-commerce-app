package com.example.tts_like.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.tts_like.data.mock.MockData
import com.example.tts_like.data.model.Address
import com.example.tts_like.data.model.Cart
import com.example.tts_like.data.model.CartItem
import com.example.tts_like.data.model.Coupon
import com.example.tts_like.data.model.Order
import com.example.tts_like.data.model.OrderItem
import com.example.tts_like.data.model.OrderStatus
import com.example.tts_like.data.model.Payment
import com.example.tts_like.data.model.PaymentStatus
import com.example.tts_like.data.model.Product
import com.example.tts_like.data.model.ProductSku
import com.example.tts_like.data.model.ProductStatus
import com.example.tts_like.data.model.VideoContent
import kotlin.math.max
import kotlin.math.min

object CommerceRepository {
    var cart by mutableStateOf(Cart())
        private set

    var orders by mutableStateOf(MockData.mockOrders)
        private set

    var pendingCheckoutItems by mutableStateOf<List<CartItem>>(emptyList())
        private set

    val videos: List<VideoContent> = MockData.videos
    val address: Address = MockData.mockAddress

    fun getProductsByIds(ids: List<String>): List<Product> {
        return ids.mapNotNull { id -> MockData.products.find { it.id == id } }
    }

    fun getProduct(productId: String): Product? {
        return MockData.products.find { it.id == productId }
    }

    fun recommendedProducts(excludedIds: Set<String> = emptySet()): List<Product> {
        return MockData.products
            .filter { it.status == ProductStatus.ON_SALE && it.skus.any { sku -> sku.stock > 0 } }
            .filterNot { it.id in excludedIds }
            .take(3)
    }

    fun bestCouponFor(items: List<CartItem>): Coupon? {
        val amount = items.sumOf { it.sku.price * it.quantity }
        val productIds = items.map { it.productId }.toSet()
        return MockData.coupons
            .filter { it.canUseFor(amount, productIds) }
            .maxByOrNull { it.discountAmount }
    }

    fun addToCart(product: Product, sku: ProductSku, quantity: Int = 1): ActionResult {
        validateProductSku(product, sku)?.let { return ActionResult(false, it) }
        val safeQuantity = quantity.coerceIn(1, sku.stock)
        val existing = cart.items.firstOrNull { it.skuId == sku.id }
        val nextItems = if (existing == null) {
            cart.items + CartItem(
                id = "cart_${sku.id}_${System.currentTimeMillis()}",
                productId = product.id,
                skuId = sku.id,
                product = product,
                sku = sku,
                quantity = safeQuantity,
            )
        } else {
            cart.items.map { item ->
                if (item.skuId == sku.id) {
                    item.copy(quantity = min(item.quantity + safeQuantity, sku.stock), selected = true)
                } else {
                    item
                }
            }
        }
        cart = cart.copy(items = nextItems.validateStock())
        return ActionResult(true, "已加入购物车")
    }

    fun updateQuantity(cartItemId: String, delta: Int) {
        cart = cart.copy(
            items = cart.items.map { item ->
                if (item.id == cartItemId) {
                    item.copy(quantity = max(1, min(item.quantity + delta, item.sku.stock)))
                } else {
                    item
                }
            }.validateStock()
        )
    }

    fun removeCartItem(cartItemId: String) {
        cart = cart.copy(items = cart.items.filterNot { it.id == cartItemId })
    }

    fun toggleSelected(cartItemId: String) {
        cart = cart.copy(
            items = cart.items.map { item ->
                if (item.id == cartItemId && item.invalidReason == null) {
                    item.copy(selected = !item.selected)
                } else {
                    item
                }
            }
        )
    }

    fun toggleAllSelected(selected: Boolean) {
        cart = cart.copy(
            items = cart.items.map { item ->
                if (item.invalidReason == null) item.copy(selected = selected) else item
            }
        )
    }

    fun checkoutSelectedItems(): ActionResult {
        val selected = cart.selectedItems
        if (selected.isEmpty()) return ActionResult(false, "请先选择可结算商品")
        validateCheckoutItems(selected)?.let { return ActionResult(false, it) }
        pendingCheckoutItems = selected
        return ActionResult(true)
    }

    fun checkoutBuyNow(product: Product, sku: ProductSku): ActionResult {
        validateProductSku(product, sku)?.let { return ActionResult(false, it) }
        pendingCheckoutItems = listOf(
            CartItem(
                id = "buy_now_${sku.id}",
                productId = product.id,
                skuId = sku.id,
                product = product,
                sku = sku,
                quantity = 1,
            )
        )
        return ActionResult(true)
    }

    fun createOrderFromPending(): Order? {
        val checkoutItems = pendingCheckoutItems
        if (checkoutItems.isEmpty()) return null
        if (validateCheckoutItems(checkoutItems) != null) return null

        val totalAmount = checkoutItems.sumOf { it.sku.price * it.quantity }
        val coupon = bestCouponFor(checkoutItems)
        val discount = coupon?.discountAmount ?: 0.0
        val shipping = if (totalAmount >= 99.0) 0.0 else 8.0
        val orderId = "order_${System.currentTimeMillis()}"
        val order = Order(
            id = orderId,
            orderNo = "LC${System.currentTimeMillis().toString().takeLast(8)}",
            userId = "user_1",
            address = address,
            items = checkoutItems.mapIndexed { index, item ->
                OrderItem(
                    id = "order_item_${index + 1}",
                    productId = item.productId,
                    skuId = item.skuId,
                    productTitle = item.product.title,
                    coverUrl = item.product.coverUrl,
                    skuSpecs = item.sku.specs,
                    price = item.sku.price,
                    quantity = item.quantity,
                    subtotal = item.sku.price * item.quantity,
                )
            },
            totalAmount = totalAmount,
            discountAmount = discount,
            shippingAmount = shipping,
            payAmount = max(0.0, totalAmount - discount + shipping),
            status = OrderStatus.PENDING_PAYMENT,
            payExpireAt = System.currentTimeMillis() + 15 * 60 * 1000,
            coupon = coupon,
        )

        orders = listOf(order) + orders
        cart = cart.copy(items = cart.items.filterNot { cartItem ->
            checkoutItems.any { it.id == cartItem.id }
        })
        pendingCheckoutItems = emptyList()
        return order
    }

    fun getOrderById(orderId: String): Order? {
        return orders.find { it.id == orderId }
    }

    fun getOrderByNo(orderNo: String): Order? {
        return orders.find { it.orderNo == orderNo }
    }

    fun payOrder(orderNo: String, success: Boolean): Payment? {
        val order = getOrderByNo(orderNo) ?: return null
        if (order.status != OrderStatus.PENDING_PAYMENT || isPaymentExpired(order)) return null
        val nextStatus = if (success) OrderStatus.PAID else OrderStatus.PENDING_PAYMENT
        val paidAt = if (success) System.currentTimeMillis() else null
        orders = orders.map {
            if (it.orderNo == orderNo) it.copy(status = nextStatus, paidAt = paidAt) else it
        }
        return Payment(
            id = "pay_${order.id}",
            orderId = order.id,
            paymentNo = "PAY${System.currentTimeMillis().toString().takeLast(8)}",
            amount = order.payAmount,
            status = if (success) PaymentStatus.SUCCESS else PaymentStatus.FAILED,
            failedReason = if (success) null else "模拟支付失败：余额不足或网络中断",
            paidAt = paidAt,
        )
    }

    fun expireOrder(orderNo: String) {
        orders = orders.map { order ->
            if (order.orderNo == orderNo && order.status == OrderStatus.PENDING_PAYMENT) {
                order.copy(status = OrderStatus.CANCELLED)
            } else {
                order
            }
        }
    }

    fun isPaymentExpired(order: Order): Boolean {
        return order.payExpireAt?.let { it <= System.currentTimeMillis() } ?: false
    }

    fun canPay(order: Order): Boolean {
        return order.status == OrderStatus.PENDING_PAYMENT && !isPaymentExpired(order)
    }

    fun pendingCheckoutError(): String? {
        return validateCheckoutItems(pendingCheckoutItems)
    }

    private fun validateCheckoutItems(items: List<CartItem>): String? {
        if (items.isEmpty()) return "暂无待结算商品"
        items.forEach { item ->
            val currentProduct = getProduct(item.productId) ?: return "商品已不存在，请返回购物车重新选择"
            val currentSku = currentProduct.skus.find { it.id == item.skuId }
                ?: return "${item.product.title} 的规格已失效"
            validateProductSku(currentProduct, currentSku)?.let { return it }
            if (item.quantity > currentSku.stock) return "${item.product.title} 库存不足，仅剩 ${currentSku.stock} 件"
            if (item.sku.price != currentSku.price) return "${item.product.title} 价格已变化，请返回购物车重新确认"
        }
        return null
    }

    private fun validateProductSku(product: Product, sku: ProductSku): String? {
        if (product.status != ProductStatus.ON_SALE) return "${product.title} 已下架"
        if (sku.stock <= 0) return "${product.title} 当前规格库存不足"
        return null
    }

    private fun List<CartItem>.validateStock(): List<CartItem> {
        return map { item ->
            val reason = when {
                item.product.status != ProductStatus.ON_SALE -> "商品已下架"
                item.sku.stock <= 0 -> "库存不足"
                item.quantity > item.sku.stock -> "仅剩 ${item.sku.stock} 件"
                else -> null
            }
            item.copy(invalidReason = reason, selected = if (reason == null) item.selected else false)
        }
    }

    internal fun resetForTesting() {
        cart = Cart()
        orders = MockData.mockOrders
        pendingCheckoutItems = emptyList()
    }
}

data class ActionResult(
    val success: Boolean,
    val message: String? = null,
)
