package com.example.tts_like.data.model

data class CartItem(
    val id: String,
    val productId: String,
    val skuId: String,
    val product: Product,
    val sku: ProductSku,
    val quantity: Int,
    val selected: Boolean = true,
    val invalidReason: String? = null,
)

data class Cart(
    val items: List<CartItem> = emptyList(),
) {
    val selectedItems get() = items.filter { it.selected && it.invalidReason == null }
    val totalPrice get() = selectedItems.sumOf { it.sku.price * it.quantity }
    val totalCount get() = items.sumOf { it.quantity }
    val selectedCount get() = selectedItems.sumOf { it.quantity }
    val hasSelectedItems get() = selectedItems.isNotEmpty()
}
