package com.example.tts_like.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "commerce_cache")
data class CommerceCacheEntity(
    @PrimaryKey val key: String,
    val payload: String,
    val updatedAt: Long,
)

@Dao
interface CommerceCacheDao {
    @Query("SELECT * FROM commerce_cache WHERE key = :key")
    suspend fun get(key: String): CommerceCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: CommerceCacheEntity)

    @Query("DELETE FROM commerce_cache")
    suspend fun clear()
}
