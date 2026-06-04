package com.example.tts_like.feature.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
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
    val featuredProduct = products.firstOrNull()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF171410), Color(0xFF31241C), Color(0xFF100E0D))
                )
            ),
    ) {
        LiveVideoPlaceholder()

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 44.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LiveHeader(author = video.author)
            PromoTicker()
        }

        LiveActionRail(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 14.dp, top = 80.dp),
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, end = 224.dp, bottom = 94.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = video.title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            LiveComment("小北", "短袖有偏大吗？")
            LiveComment("Luna", "红色 L 还有货吗")
            LiveComment("主播", "喜欢宽松一点的建议拍大一码～", emphasized = true)
        }

        featuredProduct?.let { product ->
            ProductFloatCard(
                product = product,
                productCount = products.size,
                onOpenDetail = { selectedProduct = product },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 14.dp, bottom = 100.dp),
            )
        }

        LiveBottomBar(
            cartCount = CommerceRepository.cart.totalCount,
            onOpenCart = { navController.navigate(Screen.Cart.route) },
            onOpenOrders = { navController.navigate(Screen.OrderList.route) },
            modifier = Modifier.align(Alignment.BottomCenter),
        )

        actionMessage?.let { message ->
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 78.dp),
                color = Color(0xE622211F),
                shape = RoundedCornerShape(18.dp),
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        selectedProduct?.let { product ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.46f))
                    .clickable { selectedProduct = null },
            )
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
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Composable
private fun LiveVideoPlaceholder() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(260.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(Color(0x50FF8A3D), Color.Transparent)
                    )
                ),
        )
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("LIVE", color = Color(0xFFFF7A34), fontWeight = FontWeight.Black)
            Spacer(Modifier.height(6.dp))
            Text("直播画面占位", color = Color.White.copy(alpha = 0.56f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun LiveHeader(author: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFFF7A34)),
            contentAlignment = Alignment.Center,
        ) {
            Text("穿", color = Color.White, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.width(9.dp))
        Column {
            Text("@$author", color = Color.White, fontWeight = FontWeight.Bold)
            Text("1.2 万人正在观看", color = Color.White.copy(alpha = 0.72f), style = MaterialTheme.typography.bodySmall)
        }
        Spacer(Modifier.width(10.dp))
        Surface(color = Color(0xFFE94034), shape = RoundedCornerShape(14.dp)) {
            Text("直播中", modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp), color = Color.White, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun PromoTicker() {
    Surface(color = Color(0xC933261D), shape = RoundedCornerShape(6.dp)) {
        Text(
            text = "限时福利  满 99 减 20 · 下单享 7 天无理由",
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            color = Color(0xFFFFD8B7),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun LiveActionRail(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(17.dp)) {
        LiveAction("♡", "3.8万")
        LiveAction("↗", "分享")
        LiveAction("⋯", "更多")
    }
}

@Composable
private fun LiveAction(icon: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(shape = CircleShape, color = Color.Black.copy(alpha = 0.34f)) {
            Text(icon, modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp), color = Color.White, style = MaterialTheme.typography.titleLarge)
        }
        Text(label, color = Color.White, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun LiveComment(author: String, content: String, emphasized: Boolean = false) {
    Surface(
        color = if (emphasized) Color(0xB34B2D20) else Color.Black.copy(alpha = 0.28f),
        shape = RoundedCornerShape(10.dp),
    ) {
        Text(
            text = "$author  $content",
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
            color = if (emphasized) Color(0xFFFFD4B0) else Color.White,
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun LiveBottomBar(
    cartCount: Int,
    onOpenCart: () -> Unit,
    onOpenOrders: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(listOf(Color.Transparent, Color(0xC8000000)))
            )
            .padding(horizontal = 16.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            color = Color.White.copy(alpha = 0.14f),
            shape = RoundedCornerShape(20.dp),
        ) {
            Text("说点什么...", modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), color = Color.White.copy(alpha = 0.66f), style = MaterialTheme.typography.bodySmall)
        }
        Surface(
            modifier = Modifier.clickable(onClick = onOpenOrders),
            color = Color.White.copy(alpha = 0.14f),
            shape = CircleShape,
        ) {
            Text("单", modifier = Modifier.padding(10.dp), color = Color.White, fontWeight = FontWeight.Bold)
        }
        Surface(
            modifier = Modifier.clickable(onClick = onOpenCart),
            color = Color(0xFFFF6B2C),
            shape = CircleShape,
        ) {
            Text("袋 $cartCount", modifier = Modifier.padding(horizontal = 11.dp, vertical = 10.dp), color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
