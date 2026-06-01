package com.example.tts_like.feature.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tts_like.data.repository.CommerceRepository
import com.example.tts_like.navigation.Screen

@Composable
fun CartScreen(navController: NavController) {
    val cart = CommerceRepository.cart
    val coupon = CommerceRepository.bestCouponFor(cart.selectedItems)
    val allSelected = cart.items.isNotEmpty() && cart.items.all { it.selected || it.invalidReason != null }
    var checkoutMessage by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 92.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("购物车", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

            if (cart.items.isEmpty()) {
                Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 2.dp) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text("购物车还是空的")
                        Text("回到内容流看看本场推荐商品。", style = MaterialTheme.typography.bodySmall)
                        Button(onClick = { navController.navigate(Screen.Feed.route) }) {
                            Text("去逛逛")
                        }
                    }
                }
            } else {
                cart.items.forEach { item ->
                    CartItemRow(
                        item = item,
                        onToggle = { CommerceRepository.toggleSelected(item.id) },
                        onDecrease = { CommerceRepository.updateQuantity(item.id, -1) },
                        onIncrease = { CommerceRepository.updateQuantity(item.id, 1) },
                        onRemove = { CommerceRepository.removeCartItem(item.id) },
                    )
                }
            }

            checkoutMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Button(onClick = { navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
                Text("返回")
            }
        }

        if (cart.items.isNotEmpty()) {
            CartSummaryBar(
                cart = cart,
                coupon = coupon,
                allSelected = allSelected,
                onToggleAll = { CommerceRepository.toggleAllSelected(!allSelected) },
                onCheckout = {
                    val result = CommerceRepository.checkoutSelectedItems()
                    checkoutMessage = result.message
                    if (result.success) {
                        navController.navigate(Screen.OrderConfirm.route)
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}
