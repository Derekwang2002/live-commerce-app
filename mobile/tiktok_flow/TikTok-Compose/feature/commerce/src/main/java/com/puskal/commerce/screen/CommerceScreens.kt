package com.puskal.commerce.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.puskal.commerce.data.CommerceRepository
import com.puskal.commerce.model.CartItem
import com.puskal.commerce.model.CheckoutDraft
import com.puskal.commerce.model.Order
import com.puskal.commerce.model.OrderItem
import com.puskal.commerce.model.OrderStatus
import com.puskal.commerce.model.Product
import com.puskal.commerce.model.ProductSku
import com.puskal.commerce.navigation.buyNowOrderRoute
import com.puskal.commerce.navigation.cartOrderRoute
import com.puskal.commerce.navigation.cartRoute
import com.puskal.commerce.navigation.orderDetailRoute
import com.puskal.commerce.navigation.paymentRoute
import com.puskal.commerce.navigation.productDetailRoute
import com.puskal.core.DestinationRoute.ORDER_LIST_ROUTE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    navController: NavController,
    productId: String
) {
    val product = CommerceRepository.findProduct(productId)
    if (product == null) {
        CommerceScaffold(title = "商品详情", navController = navController) {
            EmptyState(text = "商品不存在")
        }
        return
    }
    var selectedSku by remember(product.id) {
        mutableStateOf(product.skus.firstOrNull { it.stock > 0 } ?: product.skus.first())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("商品详情") },
                navigationIcon = { TextButton(onClick = { navController.navigateUp() }) { Text("返回") } },
                actions = { TextButton(onClick = { navController.navigate(ORDER_LIST_ROUTE) }) { Text("订单") } }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        CommerceRepository.addToCart(product.id, selectedSku.id)
                        navController.navigate(cartRoute())
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("加入购物车")
                }
                Button(
                    onClick = { navController.navigate(buyNowOrderRoute(product.id, selectedSku.id)) },
                    enabled = selectedSku.stock > 0,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("立即购买")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            item {
                ProductHero(product = product)
                ProductInfo(product = product)
                SkuSelector(
                    skus = product.skus,
                    selectedSku = selectedSku,
                    onSelect = { selectedSku = it }
                )
                SellingPoints(product = product)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductSearchResultScreen(
    navController: NavController,
    query: String
) {
    val products = CommerceRepository.searchProducts(query)
    CommerceScaffold(title = "搜索：$query", navController = navController) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = products, key = { it.id }) { product ->
                ProductListCard(
                    product = product,
                    onClick = { navController.navigate(productDetailRoute(product.id)) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavController) {
    val cart by CommerceRepository.cart.collectAsState()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("购物车") },
                navigationIcon = { TextButton(onClick = { navController.navigateUp() }) { Text("返回") } }
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("已选 ${cart.selectedCount} 件", style = MaterialTheme.typography.labelMedium)
                    PriceText(price = cart.totalPrice, style = MaterialTheme.typography.titleMedium)
                }
                Button(
                    onClick = { navController.navigate(cartOrderRoute()) },
                    enabled = cart.selectedItems.isNotEmpty()
                ) {
                    Text("结算")
                }
            }
        }
    ) { padding ->
        if (cart.items.isEmpty()) {
            Box(modifier = Modifier.padding(padding)) {
                EmptyState(text = "购物车是空的")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = cart.items, key = { it.id }) { item ->
                    CartItemCard(
                        item = item,
                        onRemove = { CommerceRepository.removeCartItem(item.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderConfirmScreen(
    navController: NavController,
    source: String
) {
    val draft = CommerceRepository.checkoutDraft(source)
    CommerceScaffold(title = "确认订单", navController = navController) {
        if (draft.items.isEmpty()) {
            EmptyState(text = "没有可结算的商品")
        } else {
            OrderConfirmContent(
                draft = draft,
                onSubmit = {
                    val order = CommerceRepository.createOrder(source)
                    if (order != null) {
                        navController.navigate(paymentRoute(order.orderNo))
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    navController: NavController,
    orderNo: String
) {
    val orders by CommerceRepository.orders.collectAsState()
    val order = orders.firstOrNull { it.orderNo == orderNo }
    CommerceScaffold(title = "支付", navController = navController) {
        if (order == null) {
            EmptyState(text = "订单不存在")
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCard(title = "待支付订单") {
                    Text(order.orderNo, fontWeight = FontWeight.SemiBold)
                    PriceText(price = order.payAmount, style = MaterialTheme.typography.headlineSmall)
                }
                SummaryCard(title = "支付方式") {
                    Text("Mock Pay")
                    Text("当前为演示支付，点击后直接支付成功。", color = Color.Gray)
                }
                Button(
                    onClick = {
                        val payment = CommerceRepository.markOrderPaid(order.orderNo)
                        if (payment != null) {
                            navController.navigate(orderDetailRoute(payment.orderId))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("确认支付")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(navController: NavController) {
    val orders by CommerceRepository.orders.collectAsState()
    CommerceScaffold(title = "我的订单", navController = navController) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = orders, key = { it.id }) { order ->
                OrderCard(
                    order = order,
                    onClick = { navController.navigate(orderDetailRoute(order.id)) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    orderId: String
) {
    val orders by CommerceRepository.orders.collectAsState()
    val order = orders.firstOrNull { it.id == orderId }
    CommerceScaffold(title = "订单详情", navController = navController) {
        if (order == null) {
            EmptyState(text = "订单不存在")
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    SummaryCard(title = "订单状态") {
                        Text(order.orderNo, fontWeight = FontWeight.SemiBold)
                        Text(order.status.label(), color = order.status.color())
                    }
                }
                item {
                    AddressCard(order = order)
                }
                items(items = order.items, key = { it.id }) { item ->
                    OrderItemCard(item = item)
                }
                item {
                    SummaryCard(title = "金额") {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("商品金额")
                            PriceText(price = order.totalAmount)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("实付")
                            PriceText(price = order.payAmount, style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CommerceScaffold(
    title: String,
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = { TextButton(onClick = { navController.navigateUp() }) { Text("返回") } }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            content(PaddingValues())
        }
    }
}

@Composable
private fun ProductHero(product: Product) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .background(Color(0xFFF6F6F6))
    ) {
        AsyncImage(
            model = product.coverUrl,
            contentDescription = product.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text("已售 ${product.salesCount}", color = Color.White)
        }
    }
}

@Composable
private fun ProductInfo(product: Product) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PriceRow(product.price, product.originalPrice)
        Text(product.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(product.description, style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
    }
}

@Composable
private fun SkuSelector(
    skus: List<ProductSku>,
    selectedSku: ProductSku,
    onSelect: (ProductSku) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("规格", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        skus.forEach { sku ->
            val selected = sku.id == selectedSku.id
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = sku.stock > 0) { onSelect(sku) },
                shape = RoundedCornerShape(8.dp),
                color = if (selected) Color(0xFFFFEDF2) else Color(0xFFF7F7F7)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(sku.specs.entries.joinToString(" / ") { "${it.key}:${it.value}" })
                        Text("库存 ${sku.stock}", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
                    }
                    PriceText(price = sku.price)
                }
            }
        }
    }
}

@Composable
private fun SellingPoints(product: Product) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("卖点", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        product.aiSellingPoints.forEach { point ->
            Text("• $point", color = Color.DarkGray)
        }
    }
}

@Composable
private fun ProductListCard(product: Product, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AsyncImage(
                model = product.coverUrl,
                contentDescription = product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F1F1))
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(product.title, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(product.description, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)
                PriceRow(product.price, product.originalPrice)
            }
        }
    }
}

@Composable
private fun CartItemCard(item: CartItem, onRemove: () -> Unit) {
    SummaryCard(title = item.product.title) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AsyncImage(
                model = item.product.coverUrl,
                contentDescription = item.product.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(74.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F1F1))
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(item.sku.specs.entries.joinToString(" / ") { "${it.key}:${it.value}" }, color = Color.Gray)
                Text("x${item.quantity}")
                PriceText(price = item.sku.price * item.quantity)
            }
            TextButton(onClick = onRemove) {
                Text("删除")
            }
        }
    }
}

@Composable
private fun OrderConfirmContent(draft: CheckoutDraft, onSubmit: () -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SummaryCard(title = "收货地址") {
                Text("${draft.address.receiverName}  ${draft.address.phone}", fontWeight = FontWeight.SemiBold)
                Text("${draft.address.province}${draft.address.city}${draft.address.district}${draft.address.detail}")
            }
        }
        items(items = draft.items, key = { it.id }) { item ->
            OrderItemCard(item = item)
        }
        item {
            SummaryCard(title = "订单金额") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("商品金额")
                    PriceText(price = draft.totalAmount)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("优惠")
                    Text("-¥${"%.2f".format(draft.discountAmount)}")
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("应付", fontWeight = FontWeight.Bold)
                    PriceText(price = draft.payAmount, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
        item {
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("提交订单")
            }
        }
    }
}

@Composable
private fun OrderCard(order: Order, onClick: () -> Unit) {
    SummaryCard(
        title = order.orderNo,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(order.status.label(), color = order.status.color())
            PriceText(price = order.payAmount)
        }
        Text(order.items.joinToString("、") { it.productTitle }, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun OrderItemCard(item: OrderItem) {
    SummaryCard(title = item.productTitle) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AsyncImage(
                model = item.coverUrl,
                contentDescription = item.productTitle,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(74.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F1F1))
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(item.skuSpecs.entries.joinToString(" / ") { "${it.key}:${it.value}" }, color = Color.Gray)
                Text("x${item.quantity}")
                PriceText(price = item.subtotal)
            }
        }
    }
}

@Composable
private fun AddressCard(order: Order) {
    SummaryCard(title = "收货地址") {
        Text("${order.address.receiverName}  ${order.address.phone}", fontWeight = FontWeight.SemiBold)
        Text("${order.address.province}${order.address.city}${order.address.district}${order.address.detail}")
    }
}

@Composable
private fun SummaryCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun EmptyState(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, color = Color.Gray)
    }
}

@Composable
private fun PriceRow(price: Double, originalPrice: Double?) {
    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PriceText(price = price, style = MaterialTheme.typography.headlineSmall)
        originalPrice?.let {
            Text("¥${"%.2f".format(it)}", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun PriceText(
    price: Double,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyLarge
) {
    Text(
        text = "¥${"%.2f".format(price)}",
        color = Color(0xFFFF2D55),
        style = style,
        fontWeight = FontWeight.Bold
    )
}

private fun OrderStatus.label(): String {
    return when (this) {
        OrderStatus.PENDING_PAYMENT -> "待支付"
        OrderStatus.PAID -> "已支付"
        OrderStatus.SHIPPING -> "配送中"
        OrderStatus.DELIVERED -> "已送达"
        OrderStatus.COMPLETED -> "已完成"
        OrderStatus.CANCELLED -> "已取消"
    }
}

private fun OrderStatus.color(): Color {
    return when (this) {
        OrderStatus.PENDING_PAYMENT -> Color(0xFFFF8A00)
        OrderStatus.PAID,
        OrderStatus.SHIPPING,
        OrderStatus.DELIVERED,
        OrderStatus.COMPLETED -> Color(0xFF12A150)
        OrderStatus.CANCELLED -> Color.Gray
    }
}
