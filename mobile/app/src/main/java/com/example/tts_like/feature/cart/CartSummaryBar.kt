package com.example.tts_like.feature.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tts_like.data.model.Cart
import com.example.tts_like.data.model.Coupon
import com.example.tts_like.feature.common.money

@Composable
fun CartSummaryBar(
    cart: Cart,
    coupon: Coupon?,
    allSelected: Boolean,
    onToggleAll: () -> Unit,
    onCheckout: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxWidth(), tonalElevation = 6.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Checkbox(checked = allSelected, onCheckedChange = { onToggleAll() })
            Text("全选")
            Column(modifier = Modifier.weight(1f)) {
                Text("合计 ${money(cart.totalPrice)}", fontWeight = FontWeight.Bold)
                Text(
                    text = coupon?.let { "可用优惠：${it.title}，立减 ${money(it.discountAmount)}" } ?: "满足门槛后可自动用券",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF216E39),
                )
            }
            Button(onClick = onCheckout, enabled = cart.hasSelectedItems) {
                Text("去结算")
            }
        }
    }
}
