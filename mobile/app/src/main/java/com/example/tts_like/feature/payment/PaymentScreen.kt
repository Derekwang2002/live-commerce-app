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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tts_like.data.model.OrderStatus
import com.example.tts_like.feature.common.money
import com.example.tts_like.navigation.Screen

@Composable
fun PaymentScreen(navController: NavController, orderNo: String, viewModel: PaymentViewModel = viewModel()) {
    val order = viewModel.orderByNo(orderNo)
    var showTimeout by remember(orderNo) { mutableStateOf(false) }
    var processing by remember(orderNo) { mutableStateOf(false) }
    val paymentAvailable = order?.let(viewModel::canPay) == true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("订单支付", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

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
                PaymentCountdown(
                    expireAt = order.payExpireAt,
                    onTimeout = {
                        viewModel.expireOrder(orderNo)
                        showTimeout = true
                    },
                )
                Text("请在倒计时结束前完成支付，超时后订单将自动关闭。", style = MaterialTheme.typography.bodySmall)
            }
        }

        Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("支付方式", fontWeight = FontWeight.SemiBold)
                Text("沙箱支付 · 默认通道")
                Text("当前为演示环境，用沙箱通道验证支付成功与失败链路。")
            }
        }

        if (!paymentAvailable) {
            Text(
                text = if (order.status == OrderStatus.PAID) "订单已支付，无需重复操作" else "订单已超时，请返回订单列表重新下单",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Button(
            onClick = {
                if (processing) return@Button
                processing = true
                if (viewModel.pay(orderNo, success = true) != null) {
                    navController.navigate(Screen.PaymentSuccess.createRoute(orderNo))
                } else {
                    viewModel.expireOrder(orderNo)
                    processing = false
                    showTimeout = true
                }
            },
            enabled = paymentAvailable && !processing,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (processing) "处理中..." else "确认支付")
        }
        OutlinedButton(
            onClick = {
                if (processing) return@OutlinedButton
                processing = true
                if (viewModel.pay(orderNo, success = false) != null) {
                    navController.navigate(Screen.PaymentFailed.createRoute(orderNo))
                } else {
                    viewModel.expireOrder(orderNo)
                    processing = false
                    showTimeout = true
                }
            },
            enabled = paymentAvailable && !processing,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("演示支付失败场景")
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
            text = { Text("订单支付时间已结束，可返回内容流重新选购。") },
        )
    }
}
