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
import com.example.tts_like.feature.common.money
import com.example.tts_like.feature.common.shortTime
import com.example.tts_like.navigation.Screen

@Composable
fun OrderListScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("订单列表", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        CommerceRepository.orders.forEach { order ->
            Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(order.orderNo, fontWeight = FontWeight.SemiBold)
                    Text("状态：${order.status} · ${shortTime(order.createdAt)}", style = MaterialTheme.typography.bodySmall)
                    Text("${order.items.firstOrNull()?.productTitle ?: "商品"} 等 ${order.items.size} 件")
                    Text("实付款 ${money(order.payAmount)}", fontWeight = FontWeight.Bold)
                    if (CommerceRepository.canPay(order)) {
                        Button(onClick = { navController.navigate(Screen.Payment.createRoute(order.orderNo)) }, modifier = Modifier.fillMaxWidth()) {
                            Text("继续支付")
                        }
                    }
                    Button(onClick = { navController.navigate(Screen.OrderDetail.createRoute(order.id)) }, modifier = Modifier.fillMaxWidth()) {
                        Text("查看详情")
                    }
                }
            }
        }

        Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("返回")
        }
    }
}
