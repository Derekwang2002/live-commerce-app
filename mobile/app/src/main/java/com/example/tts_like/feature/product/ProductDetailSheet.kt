package com.example.tts_like.feature.product

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tts_like.data.model.Product
import com.example.tts_like.data.model.ProductSku
import com.example.tts_like.data.model.ProductStatus
import com.example.tts_like.feature.common.ProductImage

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductDetailSheet(
    product: Product,
    onClose: () -> Unit,
    onAddToCart: (ProductSku) -> Unit,
    onBuyNow: (ProductSku) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedSku by remember(product.id) { mutableStateOf<ProductSku?>(null) }
    var selectionAttempted by remember(product.id) { mutableStateOf(false) }
    val selected = selectedSku
    val productAvailable = product.status == ProductStatus.ON_SALE && product.skus.any { it.stock > 0 }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ProductImage(
                    url = product.coverUrl,
                    contentDescription = product.title,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(8.dp)),
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(product.aiTitle ?: product.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("¥${selected?.price?.toInt() ?: product.price.toInt()}", style = MaterialTheme.typography.titleLarge, color = Color(0xFFC2410C), fontWeight = FontWeight.Bold)
                    product.couponText?.let {
                        Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
                    }
                    Text("已售 ${product.salesCount}", style = MaterialTheme.typography.bodySmall)
                }
            }

            if (product.description.isNotBlank()) {
                Text(product.description, style = MaterialTheme.typography.bodyMedium)
            }

            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                product.guaranteeTags.forEach { tag ->
                    Text(
                        text = tag,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFEAF7ED))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color(0xFF216E39),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("规格", fontWeight = FontWeight.SemiBold)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    product.skus.forEach { sku ->
                        val enabled = sku.stock > 0
                        FilterChip(
                            selected = selectedSku?.id == sku.id,
                            enabled = enabled,
                            onClick = { selectedSku = sku },
                            label = {
                                Text("${sku.specs.values.joinToString(" / ")} · ${if (enabled) "剩 ${sku.stock}" else "无货"}")
                            },
                        )
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("AI 卖点", fontWeight = FontWeight.SemiBold)
                product.aiSellingPoints.forEach { point ->
                    Text("• $point", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = onClose, modifier = Modifier.weight(1f)) {
                    Text("收起")
                }
                OutlinedButton(
                    onClick = {
                        if (selected == null) selectionAttempted = true else onAddToCart(selected)
                    },
                    enabled = productAvailable,
                    modifier = Modifier.weight(1f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                ) {
                    Text("加购")
                }
                Button(
                    onClick = {
                        if (selected == null) selectionAttempted = true else onBuyNow(selected)
                    },
                    enabled = productAvailable,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("立即买")
                }
            }

            if (!productAvailable) {
                Text(
                    if (product.status != ProductStatus.ON_SALE) "商品已下架，暂时无法购买" else "当前商品暂无可购买规格",
                    color = Color(0xFFB3261E),
                    style = MaterialTheme.typography.bodySmall,
                )
            } else if (selectionAttempted && selected == null) {
                Text("请选择规格后再继续", color = Color(0xFFB3261E), style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(4.dp))
        }
    }
}
