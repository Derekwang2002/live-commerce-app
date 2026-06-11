package com.example.tts_like.feature.order

import androidx.lifecycle.ViewModel
import com.example.tts_like.data.model.Order
import com.example.tts_like.data.repository.CommerceRepository

class OrderListViewModel : ViewModel() {
    val orders: List<Order> get() = CommerceRepository.orders
    fun canPay(order: Order): Boolean = CommerceRepository.canPay(order)
}
