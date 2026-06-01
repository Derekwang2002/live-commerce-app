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
fun PaymentFailedScreen(navController: NavController, orderNo: String) {
    val order = CommerceRepository.getOrderByNo(orderNo)
    val retryAvailable = order?.let(CommerceRepository::canPay) == true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("支付未完成", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color(0xFFB3261E))
        Text(if (retryAvailable) "订单仍为你保留，重新支付即可继续。" else "订单已关闭，可以返回内容流重新选购。")

        order?.let {
            Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(it.orderNo, fontWeight = FontWeight.SemiBold)
                    Text("待支付 ${money(it.payAmount)}", fontWeight = FontWeight.Bold)
                    Text("失败原因：模拟支付失败或支付超时", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Button(
            onClick = { navController.navigate(Screen.Payment.createRoute(orderNo)) },
            enabled = retryAvailable,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (retryAvailable) "重新支付" else "订单已关闭")
        }
        OutlinedButton(
            onClick = {
                order?.let { navController.navigate(Screen.OrderDetail.createRoute(it.id)) }
            },
            enabled = order != null,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("返回订单")
        }
        OutlinedButton(onClick = { navController.navigate(Screen.Feed.route) }, modifier = Modifier.fillMaxWidth()) {
            Text("继续逛逛")
        }
    }
}
