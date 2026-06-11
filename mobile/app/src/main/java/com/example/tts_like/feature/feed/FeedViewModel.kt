package com.example.tts_like.feature.feed

import androidx.lifecycle.ViewModel
import com.example.tts_like.data.model.Product
import com.example.tts_like.data.model.ProductSku
import com.example.tts_like.data.model.VideoContent
import com.example.tts_like.data.repository.ActionResult
import com.example.tts_like.data.repository.CommerceRepository

class FeedViewModel : ViewModel() {
    val video: VideoContent get() = CommerceRepository.videos.first()
    fun productsForCurrentVideo(): List<Product> = CommerceRepository.getProductsByIds(video.productIds)
    val cartCount: Int get() = CommerceRepository.cart.totalCount

    fun addToCart(product: Product, sku: ProductSku): ActionResult = CommerceRepository.addToCart(product, sku)
    fun checkoutBuyNow(product: Product, sku: ProductSku): ActionResult = CommerceRepository.checkoutBuyNow(product, sku)
}
