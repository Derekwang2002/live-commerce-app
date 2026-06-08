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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tts_like.data.repository.CommerceRepository
import com.example.tts_like.feature.common.shortTime
import com.example.tts_like.navigation.Screen

@Composable
fun OrderDetailScreen(navController: NavController, orderId: String) {
    val order = CommerceRepository.getOrderById(orderId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("订单详情", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        if (order == null) {
            Text("订单不存在：$orderId")
            Button(onClick = { navController.popBackStack() }) {
                Text("返回")
            }
            return@Column
        }

        Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(order.orderNo, fontWeight = FontWeight.SemiBold)
                Text("状态：${order.status}")
                Text("下单时间：${shortTime(order.createdAt)}", style = MaterialTheme.typography.bodySmall)
                order.paidAt?.let { Text("支付时间：${shortTime(it)}", style = MaterialTheme.typography.bodySmall) }
            }
        }

        Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("商品", fontWeight = FontWeight.SemiBold)
                order.items.forEach { OrderItemRow(it) }
            }
        }

        Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("价格", fontWeight = FontWeight.SemiBold)
                PriceDetailSection(order)
            }
        }

        if (CommerceRepository.canPay(order)) {
            Button(onClick = { navController.navigate(Screen.Payment.createRoute(order.orderNo)) }, modifier = Modifier.fillMaxWidth()) {
                Text("继续支付")
            }
        }

        Button(onClick = { navController.navigate(Screen.Feed.route) }, modifier = Modifier.fillMaxWidth()) {
            Text("继续逛逛")
        }
        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("返回")
        }
    }
}
