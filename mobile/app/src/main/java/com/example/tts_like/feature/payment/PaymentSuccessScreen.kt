package com.example.tts_like.feature.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tts_like.data.repository.CommerceRepository
import com.example.tts_like.feature.common.money
import com.example.tts_like.navigation.Screen

@Composable
fun PaymentSuccessScreen(navController: NavController, orderNo: String) {
    val order = CommerceRepository.getOrderByNo(orderNo)
    val recommended = CommerceRepository.recommendedProducts(order?.items?.map { it.productId }?.toSet() ?: emptySet())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("支付成功", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFF216E39))
        Text("订单已确认，预计 48 小时内发出。成功页继续承接用户安心感和二次转化。")

        order?.let {
            Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(it.orderNo, fontWeight = FontWeight.SemiBold)
                    Text("实付款 ${money(it.payAmount)}", fontWeight = FontWeight.Bold)
                    Text("收货人：${it.address.receiverName} ${it.address.phone}", style = MaterialTheme.typography.bodySmall)
                }
            }

            Button(onClick = { navController.navigate(Screen.OrderDetail.createRoute(it.id)) }, modifier = Modifier.fillMaxWidth()) {
                Text("查看订单详情")
            }
        }

        Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("猜你喜欢", fontWeight = FontWeight.SemiBold)
                recommended.forEach { product ->
                    Text("${product.title} · ${money(product.price)}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        OutlinedButton(onClick = { navController.navigate(Screen.Feed.route) }, modifier = Modifier.fillMaxWidth()) {
            Text("继续逛逛")
        }
    }
}
