package com.example.tts_like.data.mock

import com.example.tts_like.data.model.*

object MockData {

    val skus = listOf(
        ProductSku("sku_1", "prod_1", mapOf("颜色" to "红色", "尺寸" to "M"), 99.0, 10, "SKU001"),
        ProductSku("sku_2", "prod_1", mapOf("颜色" to "红色", "尺寸" to "L"), 99.0, 3, "SKU002"),
        ProductSku("sku_3", "prod_1", mapOf("颜色" to "白色", "尺寸" to "M"), 109.0, 0, "SKU003"),
    )

    val products = listOf(
        Product(
            id = "prod_1",
            title = "春季碎花连衣裙",
            price = 99.0,
            originalPrice = 199.0,
            coverUrl = "https://picsum.photos/400/400?random=1",
            skus = skus,
            aiSellingPoints = listOf("显瘦版型", "优质面料", "多场合适用"),
        ),
        Product(
            id = "prod_2",
            title = "韩版休闲卫衣",
            price = 129.0,
            coverUrl = "https://picsum.photos/400/400?random=2",
            skus = listOf(
                ProductSku("sku_4", "prod_2", mapOf("颜色" to "黑色"), 129.0, 20, "SKU004"),
            ),
        ),
    )

    val videos = listOf(
        VideoContent(
            id = "v1",
            title = "春季穿搭分享",
            author = "时尚博主小李",
            videoUrl = "",
            productIds = listOf("prod_1", "prod_2"),
        ),
        VideoContent(
            id = "v2",
            title = "居家好物推荐",
            author = "生活达人",
            videoUrl = "",
            productIds = listOf("prod_2"),
        ),
    )

    val mockAddress = Address(
        id = "addr_1",
        receiverName = "Derek Wang",
        phone = "138-0000-0000",
        province = "广东省",
        city = "深圳市",
        district = "南山区",
        detail = "科技园南区 Demo Street 123",
        isDefault = true,
    )

    val mockOrders = listOf(
        Order(
            id = "order_1",
            orderNo = "LC20240522001",
            userId = "user_1",
            address = mockAddress,
            items = listOf(
                OrderItem(
                    id = "item_1",
                    productId = "prod_1",
                    skuId = "sku_1",
                    productTitle = "春季碎花连衣裙",
                    coverUrl = "https://picsum.photos/400/400?random=1",
                    skuSpecs = mapOf("颜色" to "红色", "尺寸" to "M"),
                    price = 99.0,
                    quantity = 1,
                    subtotal = 99.0,
                )
            ),
            totalAmount = 99.0,
            payAmount = 99.0,
            status = OrderStatus.PENDING_PAYMENT,
            payExpireAt = System.currentTimeMillis() + 15 * 60 * 1000,
        )
    )
}