package com.example.tts_like.feature.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tts_like.data.model.Product
import com.example.tts_like.data.repository.CommerceRepository
import com.example.tts_like.feature.product.ProductDetailSheet
import com.example.tts_like.navigation.Screen

@Composable
fun FeedScreen(navController: NavController) {
    val video = CommerceRepository.videos.first()
    val products = CommerceRepository.getProductsByIds(video.productIds)
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var actionMessage by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                color = Color.Transparent,
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF111827), Color(0xFF334155), Color(0xFF7C2D12))
                            )
                        )
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text("TTS-Like", color = Color.White, style = MaterialTheme.typography.labelLarge)
                    Text(video.title, color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text("@${video.author} 正在讲解本场同款商品", color = Color(0xFFE5E7EB))
                    Text("视频播放占位：后续接入内容流播放器。", color = Color(0xFFD1D5DB), style = MaterialTheme.typography.bodySmall)
                }
            }

            Surface(shape = RoundedCornerShape(8.dp), tonalElevation = 2.dp) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("本场商品", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("讲解同款已整理好，可直接加购，也可以查看规格和优惠。", style = MaterialTheme.typography.bodySmall)
                }
            }

            products.forEach { product ->
                ProductFloatCard(
                    product = product,
                    onOpenDetail = { selectedProduct = product },
                    onQuickAdd = {
                        val sku = product.skus.firstOrNull { it.stock > 0 }
                        actionMessage = if (sku == null) {
                            "当前商品暂时无货"
                        } else {
                            CommerceRepository.addToCart(product, sku).message
                        }
                    },
                )
            }

            actionMessage?.let { Text(it, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall) }

            Button(
                onClick = { navController.navigate(Screen.Cart.route) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("去购物车（${CommerceRepository.cart.totalCount}）")
            }
            Button(
                onClick = { navController.navigate(Screen.OrderList.route) },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("查看订单")
            }
        }

        selectedProduct?.let { product ->
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
            ) {
                ProductDetailSheet(
                    product = product,
                    onClose = { selectedProduct = null },
                    onAddToCart = { sku ->
                        val result = CommerceRepository.addToCart(product, sku)
                        actionMessage = result.message
                        if (result.success) selectedProduct = null
                    },
                    onBuyNow = { sku ->
                        val result = CommerceRepository.checkoutBuyNow(product, sku)
                        actionMessage = result.message
                        if (result.success) {
                            selectedProduct = null
                            navController.navigate(Screen.OrderConfirm.route)
                        }
                    },
                )
            }
        }
    }
}
