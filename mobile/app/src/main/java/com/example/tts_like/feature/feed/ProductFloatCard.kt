package com.example.tts_like.feature.feed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tts_like.data.model.Product
import com.example.tts_like.data.model.ProductStatus
import com.example.tts_like.feature.common.ProductImage

@Composable
fun ProductFloatCard(
    product: Product,
    productCount: Int,
    onOpenDetail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val available = product.status == ProductStatus.ON_SALE && product.skus.any { it.stock > 0 }

    Surface(
        modifier = modifier
            .width(204.dp)
            .clickable(onClick = onOpenDetail),
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.96f),
        shadowElevation = 8.dp,
    ) {
        Column {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                ProductImage(
                    url = product.coverUrl,
                    contentDescription = product.title,
                    modifier = Modifier
                        .size(66.dp)
                        .clip(RoundedCornerShape(8.dp)),
                )
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = "讲解中",
                        color = Color(0xFFFF5A1F),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = product.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = if (available) "¥${product.price.toInt()}" else "暂时无货",
                        color = if (available) Color(0xFFE64A19) else Color(0xFFB3261E),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Black,
                    )
                }
            }
            Surface(color = Color(0xFFFFF1E8)) {
                Text(
                    text = "购物袋共 $productCount 件商品  ·  点击查看",
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                    color = Color(0xFF9A3412),
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }
    }
}
