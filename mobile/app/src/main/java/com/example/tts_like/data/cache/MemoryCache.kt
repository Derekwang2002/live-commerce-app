package com.example.tts_like.data.cache

class MemoryCache<T> {
    private val values = mutableMapOf<String, CacheEntry<T>>()

    fun get(key: String): T? {
        val entry = values[key] ?: return null
        return if (entry.expireAtMillis > System.currentTimeMillis()) entry.value else null
    }

    fun put(key: String, value: T, ttlMillis: Long) {
        values[key] = CacheEntry(value = value, expireAtMillis = System.currentTimeMillis() + ttlMillis)
    }

    fun clear() {
        values.clear()
    }
}

private data class CacheEntry<T>(
    val value: T,
    val expireAtMillis: Long,
)
