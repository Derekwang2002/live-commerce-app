package com.example.tts_like.feature.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tts_like.data.model.Order
import com.example.tts_like.feature.common.money

@Composable
fun PriceDetailSection(order: Order, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        PriceLine("商品金额", money(order.totalAmount))
        PriceLine("优惠", "-${money(order.discountAmount)}", Color(0xFF216E39))
        PriceLine("运费", if (order.shippingAmount == 0.0) "免运费" else money(order.shippingAmount))
        order.coupon?.let {
            Text("${it.title}：${it.expiresInText}", color = Color(0xFF216E39), style = MaterialTheme.typography.bodySmall)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("实付款", fontWeight = FontWeight.Bold)
            Text(money(order.payAmount), fontWeight = FontWeight.Bold, color = Color(0xFFC2410C))
        }
    }
}

@Composable
private fun PriceLine(label: String, value: String, color: Color = Color.Unspecified) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = color, style = MaterialTheme.typography.bodyMedium)
    }
}
