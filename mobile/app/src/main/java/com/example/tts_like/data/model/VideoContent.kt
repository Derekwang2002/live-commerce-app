package com.example.tts_like.data.model

data class VideoContent(
    val id: String,
    val title: String,
    val author: String,
    val videoUrl: String,
    val coverUrl: String = "",
    val productIds: List<String> = emptyList(),
)