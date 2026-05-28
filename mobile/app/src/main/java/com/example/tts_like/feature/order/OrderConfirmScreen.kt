package com.example.tts_like.feature.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tts_like.data.model.Order
import com.example.tts_like.data.repository.CommerceRepository
import com.example.tts_like.feature.common.money
import com.example.tts_like.navigation.Screen

@Composable
fun OrderConfirmScreen(navController: NavController) {
    val pendingItems = CommerceRepository.pendingCheckoutItems
    val previewOrder = buildPreviewOrder()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("确认订单", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        if (pendingItems.isEmpty() || previewOrder == null) {
            Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text("暂无待结算商品")
                    Button(onClick = { navController.navigate(Screen.Feed.route) }) {
                        Text("返回内容流")
                    }
                }
            }
            return@Column
        }

        Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                val address = previewOrder.address
                Text("收货地址", fontWeight = FontWeight.SemiBold)
                Text("${address.receiverName} ${address.phone}")
                Text("${address.province}${address.city}${address.district}${address.detail}", style = MaterialTheme.typography.bodySmall)
            }
        }

        Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("商品清单", fontWeight = FontWeight.SemiBold)
                previewOrder.items.forEach { item -> OrderItemRow(item) }
            }
        }

        Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("价格明细", fontWeight = FontWeight.SemiBold)
                PriceDetailSection(previewOrder)
                Text("优惠已自动选择最优券，减少用户决策成本。", style = MaterialTheme.typography.bodySmall)
            }
        }

        Button(
            onClick = {
                val order = CommerceRepository.createOrderFromPending()
                if (order != null) navController.navigate(Screen.Payment.createRoute(order.orderNo))
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("提交订单并支付 ${money(previewOrder.payAmount)}")
        }
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("返回")
        }
    }
}

@Composable
private fun buildPreviewOrder(): Order? {
    val items = CommerceRepository.pendingCheckoutItems
    if (items.isEmpty()) return null
    val total = items.sumOf { it.sku.price * it.quantity }
    val coupon = CommerceRepository.bestCouponFor(items)
    val shipping = if (total >= 99.0) 0.0 else 8.0
    return Order(
        id = "preview",
        orderNo = "PREVIEW",
        userId = "user_1",
        address = CommerceRepository.address,
        items = items.mapIndexed { index, item ->
            com.example.tts_like.data.model.OrderItem(
                id = "preview_$index",
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
        totalAmount = total,
        discountAmount = coupon?.discountAmount ?: 0.0,
        shippingAmount = shipping,
        payAmount = kotlin.math.max(0.0, total - (coupon?.discountAmount ?: 0.0) + shipping),
        status = com.example.tts_like.data.model.OrderStatus.PENDING_PAYMENT,
        coupon = coupon,
    )
}
