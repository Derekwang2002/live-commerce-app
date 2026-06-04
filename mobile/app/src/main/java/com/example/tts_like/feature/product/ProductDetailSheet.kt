package com.example.tts_like.feature.product

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextDecoration
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
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.76f),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 8.dp, top = 6.dp),
            ) {
                Spacer(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(width = 38.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFFD6D3D1))
                )
                IconButton(onClick = onClose, modifier = Modifier.align(Alignment.CenterEnd)) {
                    Text("×", color = Color(0xFF6B7280), style = MaterialTheme.typography.titleLarge)
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ProductImage(
                        url = product.coverUrl,
                        contentDescription = product.title,
                        modifier = Modifier
                            .size(104.dp)
                            .clip(RoundedCornerShape(10.dp)),
                    )
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(product.aiTitle ?: product.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            Text(
                                "¥${selected?.price?.toInt() ?: product.price.toInt()}",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color(0xFFE64A19),
                                fontWeight = FontWeight.Black,
                            )
                            product.originalPrice?.let {
                                Text(
                                    "¥${it.toInt()}",
                                    color = Color(0xFF9CA3AF),
                                    style = MaterialTheme.typography.bodySmall,
                                    textDecoration = TextDecoration.LineThrough,
                                )
                            }
                        }
                        Text("已售 ${product.salesCount}  ·  库存实时更新", color = Color(0xFF6B7280), style = MaterialTheme.typography.bodySmall)
                    }
                }

                product.couponText?.let {
                    Surface(color = Color(0xFFFFF1E8), shape = RoundedCornerShape(8.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 9.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text("直播间优惠", color = Color(0xFF9A3412), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            Text(it, color = Color(0xFFE64A19), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                if (product.description.isNotBlank()) {
                    Text(product.description, color = Color(0xFF4B5563), style = MaterialTheme.typography.bodyMedium)
                }

                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    product.guaranteeTags.forEach { tag ->
                        Text(
                            text = "✓ $tag",
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
                    Text("选择规格", fontWeight = FontWeight.Bold)
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

                Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(10.dp)) {
                    Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("直播间推荐理由", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        product.aiSellingPoints.forEach { point ->
                            Text("• $point", color = Color(0xFF4B5563), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (!productAvailable) {
                    Text(
                        if (product.status != ProductStatus.ON_SALE) "商品已下架，暂时无法购买" else "当前商品暂无可购买规格",
                        color = Color(0xFFB3261E),
                        style = MaterialTheme.typography.bodySmall,
                    )
                } else if (selectionAttempted && selected == null) {
                    Text("请先选择规格后再继续", color = Color(0xFFB3261E), style = MaterialTheme.typography.bodySmall)
                } else {
                    Text("选择规格后即可享受直播间优惠", color = Color(0xFF9A3412), style = MaterialTheme.typography.bodySmall)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = {
                            if (selected == null) selectionAttempted = true else onAddToCart(selected)
                        },
                        enabled = productAvailable,
                        modifier = Modifier.weight(1f),
                        border = BorderStroke(1.dp, Color(0xFFFF6B2C)),
                    ) {
                        Text("加入购物袋", color = Color(0xFFE64A19))
                    }
                    Button(
                        onClick = {
                            if (selected == null) selectionAttempted = true else onBuyNow(selected)
                        },
                        enabled = productAvailable,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("立即购买")
                    }
                }
            }
        }
    }
}
