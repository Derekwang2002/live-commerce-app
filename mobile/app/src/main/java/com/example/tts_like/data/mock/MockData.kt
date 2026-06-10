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
            title = "重磅埃及短袖",
            description = "简短推销话术mock",
            price = 99.0,
            originalPrice = 199.0,
            coverUrl = "https://images.unsplash.com/photo-1714070700737-24acfe6b957c?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3wzNzA1NTl8MHwxfGFsbHx8fHx8fHx8fDE3Nzk5ODYzNjJ8&ixlib=rb-4.1.0&q=80&w=400",
            skus = skus,
            salesCount = 8312,
            aiTitle = "重磅埃及短袖/直播间限时价",
            aiSellingPoints = listOf("材质好", "版型好", "直播间下单可叠加满减券"),
            tags = listOf("直播热卖", "限时 5 折", "低库存"),
            guaranteeTags = listOf("7 天无理由", "极速退款", "正品保障"),
            couponText = "满 119 减 20",
        ),
        Product(
            id = "prod_2",
            title = "线下大标品短裤",
            description = "宽松休闲，彰显松弛感",
            price = 129.0,
            originalPrice = 169.0,
            coverUrl = "https://images.unsplash.com/photo-1740512922260-543b1b83c986?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3wzNzA1NTl8MHwxfGFsbHx8fHx8fHx8fDE3Nzk5ODcyNDd8&ixlib=rb-4.1.0&q=80&w=200",
            salesCount = 2460,
            skus = listOf(
                ProductSku("sku_4", "prod_2", mapOf("颜色" to "黑色"), 129.0, 20, "SKU004"),
                ProductSku("sku_5", "prod_2", mapOf("颜色" to "灰色"), 129.0, 2, "SKU005"),
            ),
            aiSellingPoints = listOf("不挑身材", "颜色百搭"),
            tags = listOf("家人福利", "大标品"),
            guaranteeTags = listOf("运费险", "7 天无理由"),
            couponText = "满 129 减 15",
        ),
        Product(
            id = "prod_3",
            title = "便携补光化妆镜",
            description = "三档补光，直播同款小物，适合宿舍、旅行和通勤包携带。",
            price = 69.0,
            originalPrice = 99.0,
            coverUrl = "https://picsum.photos/400/400?random=3",
            salesCount = 12980,
            skus = listOf(
                ProductSku("sku_6", "prod_3", mapOf("颜色" to "奶油白"), 69.0, 6, "SKU006"),
                ProductSku("sku_7", "prod_3", mapOf("颜色" to "樱花粉"), 69.0, 0, "SKU007"),
            ),
            aiSellingPoints = listOf("三档自然光，补妆不偏色", "可折叠支架，桌面和手持都方便", "下单送收纳袋"),
            tags = listOf("猜你喜欢", "送收纳袋"),
            guaranteeTags = listOf("坏损包退", "极速退款"),
            couponText = "第二件 8 折",
        ),
    )

    val videos = listOf(
        VideoContent(
            id = "v1",
            title = "夏天怎么穿？",
            author = "西海岸穿搭哥",
            videoUrl = "",
            productIds = listOf("prod_1", "prod_2"),
        ),
        VideoContent(
            id = "v2",
            title = "居家好物推荐",
            author = "生活达人",
            videoUrl = "",
            productIds = listOf("prod_2", "prod_3"),
        ),
    )

    val coupons = listOf(
        Coupon("coupon_1", "直播间专享券", threshold = 99.0, discountAmount = 20.0, expiresInText = "今日 23:59 过期"),
        Coupon("coupon_2", "穿搭组合券", threshold = 188.0, discountAmount = 35.0, expiresInText = "还剩 2 天", productIds = listOf("prod_1", "prod_2")),
    )

    val mockAddress = Address(
        id = "addr_1",
        receiverName = "Derek Wang",
        phone = "123-0000-0000",
        province = "广东省",
        city = "深圳市",
        district = "南山区",
        detail = "科技园南区 Street 123",
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
                    productTitle = "重磅埃及短袖",
                    coverUrl = "https://images.unsplash.com/photo-1714070700737-24acfe6b957c?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=M3wzNzA1NTl8MHwxfGFsbHx8fHx8fHx8fDE3Nzk5ODYzNjJ8&ixlib=rb-4.1.0&q=80&w=400",
                    skuSpecs = mapOf("颜色" to "红色", "尺寸" to "M"),
                    price = 99.0,
                    quantity = 1,
                    subtotal = 99.0,
                )
            ),
            totalAmount = 99.0,
            discountAmount = 20.0,
            payAmount = 79.0,
            status = OrderStatus.PENDING_PAYMENT,
            payExpireAt = System.currentTimeMillis() + 15 * 60 * 1000,
            coupon = coupons.first(),
        )
    )
}
