package com.puskal.data.repository.live

import com.puskal.data.api.live.LiveRoomApi
import com.puskal.data.api.live.MockLiveRoomApi
import com.puskal.data.model.live.LiveRoomModel
import javax.inject.Inject

class LiveRoomRepository @Inject constructor() {
    private val api: LiveRoomApi = MockLiveRoomApi()

    suspend fun getLiveRoom(roomId: String): LiveRoomModel {
        return api.fetchLiveRoom(roomId)
    }
}
