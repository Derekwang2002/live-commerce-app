package com.puskal.data.model.live

import com.puskal.data.model.UserModel

data class LiveRoomModel(
    val roomId: String,
    val anchor: UserModel,
    val title: String,
    val liveStreamUrl: String,
    val onlineCount: Long,
    val statusTags: List<String>,
    val comments: List<LiveComment>,
    val currentProduct: LiveProduct?
)

data class LiveComment(
    val id: String,
    val userName: String,
    val text: String
)

data class LiveProduct(
    val id: String,
    val name: String,
    val priceText: String
)
