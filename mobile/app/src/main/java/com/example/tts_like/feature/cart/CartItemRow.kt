package com.example.tts_like.feature.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tts_like.data.model.CartItem
import com.example.tts_like.feature.common.money

@Composable
fun CartItemRow(
    item: CartItem,
    onToggle: () -> Unit,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), tonalElevation = 2.dp) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Checkbox(checked = item.selected, enabled = item.invalidReason == null, onCheckedChange = { onToggle() })
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(item.product.title, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(item.sku.specs.values.joinToString(" / "), style = MaterialTheme.typography.bodySmall)
                Text(money(item.sku.price), color = Color(0xFFC2410C), fontWeight = FontWeight.Bold)
                item.invalidReason?.let {
                    Text(it, color = Color(0xFFB3261E), style = MaterialTheme.typography.bodySmall)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrease) { Text("-") }
                Text(item.quantity.toString(), fontWeight = FontWeight.SemiBold)
                IconButton(onClick = onIncrease) { Text("+") }
            }
            Spacer(Modifier.width(2.dp))
            IconButton(onClick = onRemove) { Text("删") }
        }
    }
}
