package com.example.tts_like.feature.order

import androidx.lifecycle.ViewModel
import com.example.tts_like.data.model.Order
import com.example.tts_like.data.repository.CommerceRepository

class OrderDetailViewModel : ViewModel() {
    fun orderById(orderId: String): Order? = CommerceRepository.getOrderById(orderId)
    fun canPay(order: Order): Boolean = CommerceRepository.canPay(order)
}
