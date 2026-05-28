package com.example.tts_like.feature.payment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tts_like.data.repository.CommerceRepository
import com.example.tts_like.feature.common.money
import com.example.tts_like.navigation.Screen

@Composable
fun PaymentScreen(navController: NavController, orderNo: String) {
    val order = CommerceRepository.getOrderByNo(orderNo)
    var showTimeout by remember(orderNo) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("模拟支付", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        if (order == null) {
            Text("订单不存在：$orderNo")
            Button(onClick = { navController.popBackStack() }) {
                Text("返回")
            }
            return@Column
        }

        Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(order.orderNo, fontWeight = FontWeight.SemiBold)
                Text("待支付 ${money(order.payAmount)}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                PaymentCountdown(expireAt = order.payExpireAt, onTimeout = { showTimeout = true })
                Text("倒计时提醒订单即将失效，促使高意向用户完成最后一步。", style = MaterialTheme.typography.bodySmall)
            }
        }

        Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("支付方式", fontWeight = FontWeight.SemiBold)
                Text("Mock Pay · 默认支付")
                Text("当前为课题演示，不接入真实支付。")
            }
        }

        Button(
            onClick = {
                CommerceRepository.payOrder(orderNo, success = true)
                navController.navigate(Screen.PaymentSuccess.createRoute(orderNo))
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("确认支付")
        }
        OutlinedButton(
            onClick = {
                CommerceRepository.payOrder(orderNo, success = false)
                navController.navigate(Screen.PaymentFailed.createRoute(orderNo))
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("模拟支付失败")
        }
        OutlinedButton(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
            Text("返回")
        }
    }

    if (showTimeout) {
        AlertDialog(
            onDismissRequest = { showTimeout = false },
            confirmButton = {
                Button(onClick = {
                    showTimeout = false
                    navController.navigate(Screen.PaymentFailed.createRoute(orderNo))
                }) {
                    Text("查看失败页")
                }
            },
            title = { Text("支付已超时") },
            text = { Text("订单支付时间已结束，可返回订单重新发起支付。") },
        )
    }
}
