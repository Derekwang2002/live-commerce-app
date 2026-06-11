package com.example.tts_like.feature.order

import androidx.lifecycle.ViewModel
import com.example.tts_like.data.model.Address
import com.example.tts_like.data.model.CartItem
import com.example.tts_like.data.model.Coupon
import com.example.tts_like.data.model.Order
import com.example.tts_like.data.repository.CommerceRepository

class OrderConfirmViewModel : ViewModel() {
    val pendingItems: List<CartItem> get() = CommerceRepository.pendingCheckoutItems
    val address: Address get() = CommerceRepository.address
    fun bestCoupon(): Coupon? = CommerceRepository.bestCouponFor(pendingItems)

    fun pendingCheckoutError(): String? = CommerceRepository.pendingCheckoutError()
    fun createOrderFromPending(): Order? = CommerceRepository.createOrderFromPending()
}
