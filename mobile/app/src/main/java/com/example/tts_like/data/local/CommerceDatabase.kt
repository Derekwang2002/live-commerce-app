package com.example.tts_like.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CommerceCacheEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class CommerceDatabase : RoomDatabase() {
    abstract fun cacheDao(): CommerceCacheDao

    companion object {
        @Volatile private var instance: CommerceDatabase? = null

        fun get(context: Context): CommerceDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    CommerceDatabase::class.java,
                    "commerce-cache.db",
                ).build().also { instance = it }
            }
        }
    }
}
