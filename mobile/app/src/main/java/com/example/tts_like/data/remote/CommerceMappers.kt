package com.example.tts_like.data.remote

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

fun ProductResponse.toDomain(): Product = Product(
    id = id,
    title = title,
    description = description,
    coverUrl = coverUrl,
    images = images,
    price = price,
    originalPrice = originalPrice,
    status = runCatching { ProductStatus.valueOf(status) }.getOrDefault(ProductStatus.ON_SALE),
    salesCount = salesCount,
    skus = skus.map { it.toDomain(id) },
    aiTitle = aiTitle,
    aiSellingPoints = aiSellingPoints,
    aiScript = aiScript,
    tags = tags,
    guaranteeTags = guaranteeTags,
    couponText = couponText,
    lowStockThreshold = lowStockThreshold,
)

fun ProductSkuResponse.toDomain(fallbackProductId: String = productId): ProductSku = ProductSku(
    id = id,
    productId = productId.ifBlank { fallbackProductId },
    specs = specs,
    price = price,
    stock = stock,
    skuCode = skuCode,
)

fun CartResponse.toDomain(): Cart = Cart(items = items.map { it.toDomain() })

fun CartItemResponse.toDomain(): CartItem = CartItem(
    id = id,
    productId = productId,
    skuId = skuId,
    product = product.toDomain(),
    sku = sku.toDomain(productId),
    quantity = quantity,
    selected = selected,
    invalidReason = invalidReason,
)

fun OrderResponse.toDomain(): Order = Order(
    id = id,
    orderNo = orderNo,
    userId = userId,
    address = address.toDomain(),
    items = items.map { it.toDomain() },
    totalAmount = totalAmount,
    discountAmount = discountAmount,
    shippingAmount = shippingAmount,
    payAmount = payAmount,
    status = runCatching { OrderStatus.valueOf(status) }.getOrDefault(OrderStatus.PENDING_PAYMENT),
    payExpireAt = payExpireAt,
    paidAt = paidAt,
    coupon = coupon?.toDomain(),
    createdAt = createdAt,
)

fun OrderItemResponse.toDomain(): OrderItem = OrderItem(
    id = id,
    productId = productId,
    skuId = skuId,
    productTitle = productTitle,
    coverUrl = coverUrl,
    skuSpecs = skuSpecs,
    price = price,
    quantity = quantity,
    subtotal = subtotal,
)

fun AddressResponse.toDomain(): Address = Address(
    id = id,
    receiverName = receiverName,
    phone = phone,
    province = province,
    city = city,
    district = district,
    detail = detail,
    isDefault = isDefault,
)

fun CouponResponse.toDomain(): Coupon = Coupon(
    id = id,
    title = title,
    threshold = threshold,
    discountAmount = discountAmount,
    expiresInText = expiresInText,
    productIds = productIds,
)

fun PaymentResponse.toDomain(): Payment = Payment(
    id = id,
    orderId = orderId,
    paymentNo = paymentNo,
    amount = amount,
    status = runCatching { PaymentStatus.valueOf(status) }.getOrDefault(PaymentStatus.PENDING),
    failedReason = failedReason,
    paidAt = paidAt,
)
