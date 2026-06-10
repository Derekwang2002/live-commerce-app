package com.example.tts_like.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CommerceApi {
    @GET("products/batch")
    suspend fun getProductsBatch(@Query("ids") ids: String): ProductsBatchResponse

    @GET("products/{productId}")
    suspend fun getProduct(@Path("productId") productId: String): ProductResponse

    @GET("cart")
    suspend fun getCart(): CartResponse

    @POST("cart/items")
    suspend fun addCartItem(@Body request: AddCartItemRequest): CartResponse

    @PATCH("cart/items/{cartItemId}")
    suspend fun updateCartItem(@Path("cartItemId") cartItemId: String, @Body request: UpdateCartItemRequest): CartResponse

    @DELETE("cart/items/{cartItemId}")
    suspend fun deleteCartItem(@Path("cartItemId") cartItemId: String): CartResponse

    @PATCH("cart/items/selection")
    suspend fun updateSelection(@Body request: CartSelectionRequest): CartResponse

    @GET("orders")
    suspend fun getOrders(): OrdersResponse

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest = CreateOrderRequest()): OrderResponse

    @POST("payments")
    suspend fun createPayment(@Body request: CreatePaymentRequest): PaymentResponse

    @POST("payments/{paymentNo}/mock-success")
    suspend fun mockPaymentSuccess(@Path("paymentNo") paymentNo: String): PaymentResponse

    @POST("payments/{paymentNo}/mock-fail")
    suspend fun mockPaymentFail(@Path("paymentNo") paymentNo: String): PaymentResponse
}
