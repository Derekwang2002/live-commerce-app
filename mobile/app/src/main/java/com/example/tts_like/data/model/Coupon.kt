package com.example.tts_like.data.model

data class Coupon(
    val id: String,
    val title: String,
    val threshold: Double,
    val discountAmount: Double,
    val expiresInText: String,
    val productIds: List<String> = emptyList(),
) {
    fun canUseFor(amount: Double, checkoutProductIds: Set<String>): Boolean {
        val productMatched = productIds.isEmpty() || productIds.any { it in checkoutProductIds }
        return amount >= threshold && productMatched
    }
}
