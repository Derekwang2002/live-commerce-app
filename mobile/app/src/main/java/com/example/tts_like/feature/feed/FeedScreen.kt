package com.example.tts_like.feature.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.tts_like.navigation.Screen

@Composable
fun FeedScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("内容流页面 - puyu负责")
        Button(onClick = { navController.navigate(Screen.Cart.route) }) {
            Text("去购物车")
        }
        Button(onClick = { navController.navigate(Screen.OrderList.route) }) {
            Text("去订单列表")
        }
        Button(onClick = { navController.navigate(Screen.Payment.createRoute("TEST001")) }) {
            Text("去支付页")
        }
    }
}