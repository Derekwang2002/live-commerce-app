package com.puskal.data.api.live

import com.puskal.data.model.live.LiveRoomModel

interface LiveRoomApi {
    suspend fun fetchLiveRoom(roomId: String): LiveRoomModel
}
