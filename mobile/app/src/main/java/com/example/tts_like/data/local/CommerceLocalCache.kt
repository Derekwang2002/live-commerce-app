package com.example.tts_like.data.local

import com.example.tts_like.data.remote.CartResponse
import com.example.tts_like.data.remote.OrdersResponse
import com.example.tts_like.data.remote.ProductsBatchResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CommerceLocalCache(
    private val dao: CommerceCacheDao,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    },
) {
    suspend fun getProducts(): ProductsBatchResponse? = get(PRODUCTS_KEY)
    suspend fun saveProducts(response: ProductsBatchResponse) = put(PRODUCTS_KEY, response)

    suspend fun getCart(): CartResponse? = get(CART_KEY)
    suspend fun saveCart(response: CartResponse) = put(CART_KEY, response)

    suspend fun getOrders(): OrdersResponse? = get(ORDERS_KEY)
    suspend fun saveOrders(response: OrdersResponse) = put(ORDERS_KEY, response)

    suspend fun clear() {
        dao.clear()
    }

    private suspend inline fun <reified T> get(key: String): T? {
        return dao.get(key)?.let { entity ->
            runCatching { json.decodeFromString<T>(entity.payload) }.getOrNull()
        }
    }

    private suspend inline fun <reified T> put(key: String, value: T) {
        dao.upsert(
            CommerceCacheEntity(
                key = key,
                payload = json.encodeToString(value),
                updatedAt = System.currentTimeMillis(),
            )
        )
    }

    private companion object {
        const val PRODUCTS_KEY = "products:v1"
        const val CART_KEY = "cart:v1"
        const val ORDERS_KEY = "orders:v1"
    }
}
