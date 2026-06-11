package com.example.tts_like.feature.cart

import androidx.lifecycle.ViewModel
import com.example.tts_like.data.model.Cart
import com.example.tts_like.data.model.Coupon
import com.example.tts_like.data.repository.ActionResult
import com.example.tts_like.data.repository.CommerceRepository

class CartViewModel : ViewModel() {
    val cart: Cart get() = CommerceRepository.cart
    fun bestCoupon(): Coupon? = CommerceRepository.bestCouponFor(cart.selectedItems)

    fun toggleSelected(itemId: String) = CommerceRepository.toggleSelected(itemId)
    fun updateQuantity(itemId: String, delta: Int) = CommerceRepository.updateQuantity(itemId, delta)
    fun removeCartItem(itemId: String) = CommerceRepository.removeCartItem(itemId)
    fun toggleAllSelected(value: Boolean) = CommerceRepository.toggleAllSelected(value)
    fun checkoutSelectedItems(): ActionResult = CommerceRepository.checkoutSelectedItems()
}
