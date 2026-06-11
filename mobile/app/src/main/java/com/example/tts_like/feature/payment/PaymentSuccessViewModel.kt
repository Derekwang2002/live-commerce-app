package com.example.tts_like.feature.payment

import androidx.lifecycle.ViewModel
import com.example.tts_like.data.model.Order
import com.example.tts_like.data.model.Product
import com.example.tts_like.data.repository.CommerceRepository

class PaymentSuccessViewModel : ViewModel() {
    fun orderByNo(orderNo: String): Order? = CommerceRepository.getOrderByNo(orderNo)
    fun recommendedProducts(excluded: Set<String>): List<Product> = CommerceRepository.recommendedProducts(excluded)
}
