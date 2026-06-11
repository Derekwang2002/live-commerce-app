package com.example.tts_like.feature.payment

import androidx.lifecycle.ViewModel
import com.example.tts_like.data.model.Order
import com.example.tts_like.data.repository.CommerceRepository

class PaymentFailedViewModel : ViewModel() {
    fun orderByNo(orderNo: String): Order? = CommerceRepository.getOrderByNo(orderNo)
    fun canPay(order: Order): Boolean = CommerceRepository.canPay(order)
}
