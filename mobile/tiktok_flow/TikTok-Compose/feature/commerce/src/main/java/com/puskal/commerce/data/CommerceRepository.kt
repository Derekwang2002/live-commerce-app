package com.puskal.commerce.data

import com.puskal.commerce.model.Address
import com.puskal.commerce.model.Cart
import com.puskal.commerce.model.CartItem
import com.puskal.commerce.model.CheckoutDraft
import com.puskal.commerce.model.Order
import com.puskal.commerce.model.OrderItem
import com.puskal.commerce.model.OrderStatus
import com.puskal.commerce.model.Payment
import com.puskal.commerce.model.PaymentStatus
import com.puskal.commerce.model.Product
import com.puskal.commerce.model.ProductSku
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object CommerceRepository {
    const val CART_ORDER_SOURCE = "cart"
    private const val USER_ID = "user_1"

    private val skus = listOf(
        ProductSku("sku_1", "prod_1", mapOf("颜色" to "红色", "尺码" to "M"), 99.0, 10, "SKU001"),
        ProductSku("sku_2", "prod_1", mapOf("颜色" to "红色", "尺码" to "L"), 99.0, 3, "SKU002"),
        ProductSku("sku_3", "prod_1", mapOf("颜色" to "白色", "尺码" to "M"), 109.0, 0, "SKU003"),
        ProductSku("sku_4", "prod_2", mapOf("颜色" to "黑色"), 129.0, 20, "SKU004")
    )

    private val products = listOf(
        Product(
            id = "prod_1",
            title = "春季碎花连衣裙",
            description = "轻薄透气的春季连衣裙，适合通勤、约会和日常穿搭。",
            price = 99.0,
            originalPrice = 199.0,
            coverUrl = "https://picsum.photos/400/400?random=1",
            images = listOf(
                "https://picsum.photos/600/600?random=11",
                "https://picsum.photos/600/600?random=12"
            ),
            skus = skus.filter { it.productId == "prod_1" },
            salesCount = 2184,
            aiSellingPoints = listOf("显瘦版型", "优质面料", "多场合适用")
        ),
        Product(
            id = "prod_2",
            title = "韩版休闲卫衣",
            description = "宽松版型，柔软亲肤，适合日常出街和直播间同款穿搭。",
            price = 129.0,
            originalPrice = 169.0,
            coverUrl = "https://picsum.photos/400/400?random=2",
            images = listOf(
                "https://picsum.photos/600/600?random=21",
                "https://picsum.photos/600/600?random=22"
            ),
            skus = skus.filter { it.productId == "prod_2" },
            salesCount = 936,
            aiSellingPoints = listOf("宽松舒适", "不挑身材", "直播间热卖")
        )
    )

    private val address = Address(
        id = "addr_1",
        receiverName = "Derek Wang",
        phone = "138-0000-0000",
        province = "广东省",
        city = "深圳市",
        district = "南山区",
        detail = "科技园南区 Demo Street 123",
        isDefault = true
    )

    private val initialOrder = Order(
        id = "order_1",
        orderNo = "LC20240522001",
        userId = USER_ID,
        address = address,
        items = listOf(
            OrderItem(
                id = "item_1",
                productId = "prod_1",
                skuId = "sku_1",
                productTitle = "春季碎花连衣裙",
                coverUrl = "https://picsum.photos/400/400?random=1",
                skuSpecs = mapOf("颜色" to "红色", "尺码" to "M"),
                price = 99.0,
                quantity = 1,
                subtotal = 99.0
            )
        ),
        totalAmount = 99.0,
        payAmount = 99.0,
        status = OrderStatus.PENDING_PAYMENT,
        payExpireAt = System.currentTimeMillis() + 15 * 60 * 1000
    )

    private val _cart = MutableStateFlow(Cart())
    val cart: StateFlow<Cart> = _cart

    private val _orders = MutableStateFlow(listOf(initialOrder))
    val orders: StateFlow<List<Order>> = _orders

    fun getProducts(): List<Product> = products

    fun findProduct(productId: String): Product? = products.firstOrNull { it.id == productId }

    fun searchProducts(query: String): List<Product> {
        val keyword = query.trim()
        if (keyword.isEmpty()) return products
        return products.filter { product ->
            product.title.contains(keyword, ignoreCase = true) ||
                product.description.contains(keyword, ignoreCase = true) ||
                product.aiSellingPoints.any { it.contains(keyword, ignoreCase = true) }
        }.ifEmpty { products }
    }

    fun addToCart(productId: String, skuId: String, quantity: Int = 1) {
        val product = findProduct(productId) ?: return
        val sku = product.skus.firstOrNull { it.id == skuId } ?: product.skus.firstOrNull() ?: return
        val current = _cart.value.items
        val existing = current.firstOrNull { it.productId == productId && it.skuId == sku.id }
        _cart.value = if (existing == null) {
            Cart(
                current + CartItem(
                    id = "cart_${System.currentTimeMillis()}",
                    productId = productId,
                    skuId = sku.id,
                    product = product,
                    sku = sku,
                    quantity = quantity.coerceAtLeast(1)
                )
            )
        } else {
            Cart(current.map {
                if (it.id == existing.id) it.copy(quantity = it.quantity + quantity.coerceAtLeast(1)) else it
            })
        }
    }

    fun removeCartItem(cartItemId: String) {
        _cart.value = Cart(_cart.value.items.filterNot { it.id == cartItemId })
    }

    fun createBuyNowSource(productId: String, skuId: String): String = "buy:$productId:$skuId"

    fun checkoutDraft(source: String): CheckoutDraft {
        val items = when {
            source == CART_ORDER_SOURCE -> cartItemsToOrderItems(_cart.value.selectedItems)
            source.startsWith("buy:") -> buySourceToOrderItems(source)
            else -> emptyList()
        }
        val total = items.sumOf { it.subtotal }
        return CheckoutDraft(
            source = source,
            items = items,
            address = address,
            totalAmount = total
        )
    }

    fun createOrder(source: String): Order? {
        val draft = checkoutDraft(source)
        if (draft.items.isEmpty()) return null
        val now = System.currentTimeMillis()
        val order = Order(
            id = "order_$now",
            orderNo = "LC$now",
            userId = USER_ID,
            address = draft.address,
            items = draft.items,
            totalAmount = draft.totalAmount,
            discountAmount = draft.discountAmount,
            payAmount = draft.payAmount,
            status = OrderStatus.PENDING_PAYMENT,
            payExpireAt = now + 15 * 60 * 1000,
            createdAt = now
        )
        _orders.value = listOf(order) + _orders.value
        if (source == CART_ORDER_SOURCE) {
            val selectedIds = _cart.value.selectedItems.map { it.id }.toSet()
            _cart.value = Cart(_cart.value.items.filterNot { it.id in selectedIds })
        }
        return order
    }

    fun findOrderById(orderId: String): Order? = _orders.value.firstOrNull { it.id == orderId }

    fun findOrderByNo(orderNo: String): Order? = _orders.value.firstOrNull { it.orderNo == orderNo }

    fun markOrderPaid(orderNo: String): Payment? {
        val now = System.currentTimeMillis()
        val order = findOrderByNo(orderNo) ?: return null
        val paidOrder = order.copy(status = OrderStatus.PAID, paidAt = now)
        _orders.value = _orders.value.map { if (it.id == order.id) paidOrder else it }
        return Payment(
            id = "payment_$now",
            orderId = order.id,
            paymentNo = "PAY$now",
            amount = order.payAmount,
            status = PaymentStatus.SUCCESS,
            paidAt = now
        )
    }

    private fun buySourceToOrderItems(source: String): List<OrderItem> {
        val parts = source.split(":")
        val product = findProduct(parts.getOrNull(1).orEmpty()) ?: return emptyList()
        val sku = product.skus.firstOrNull { it.id == parts.getOrNull(2) } ?: product.skus.firstOrNull() ?: return emptyList()
        return listOf(productSkuToOrderItem(product, sku, 1))
    }

    private fun cartItemsToOrderItems(items: List<CartItem>): List<OrderItem> {
        return items.map { productSkuToOrderItem(it.product, it.sku, it.quantity) }
    }

    private fun productSkuToOrderItem(product: Product, sku: ProductSku, quantity: Int): OrderItem {
        val count = quantity.coerceAtLeast(1)
        return OrderItem(
            id = "item_${product.id}_${sku.id}_${System.nanoTime()}",
            productId = product.id,
            skuId = sku.id,
            productTitle = product.title,
            coverUrl = product.coverUrl,
            skuSpecs = sku.specs,
            price = sku.price,
            quantity = count,
            subtotal = sku.price * count
        )
    }
}
