package com.example.tts_like.feature.payment

import androidx.lifecycle.ViewModel
import com.example.tts_like.data.model.Order
import com.example.tts_like.data.model.Payment
import com.example.tts_like.data.repository.CommerceRepository

class PaymentViewModel : ViewModel() {
    fun orderByNo(orderNo: String): Order? = CommerceRepository.getOrderByNo(orderNo)
    fun canPay(order: Order): Boolean = CommerceRepository.canPay(order)
    fun pay(orderNo: String, success: Boolean): Payment? = CommerceRepository.payOrder(orderNo, success)
    fun expireOrder(orderNo: String) = CommerceRepository.expireOrder(orderNo)
}
