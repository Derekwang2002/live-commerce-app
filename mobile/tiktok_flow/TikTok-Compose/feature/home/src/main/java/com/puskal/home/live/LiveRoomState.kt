package com.puskal.home.live

import com.puskal.data.model.UserModel
import com.puskal.data.model.live.LiveComment
import com.puskal.data.model.live.LiveProduct

data class LiveRoomState(
    val roomId: String = "",
    val anchor: UserModel? = null,
    val title: String = "",
    val liveStreamUrl: String = "",
    val onlineCount: Long = 0,
    val statusTags: List<String> = emptyList(),
    val comments: List<LiveComment> = emptyList(),
    val currentProduct: LiveProduct? = null,
    val isLoading: Boolean = true
)
