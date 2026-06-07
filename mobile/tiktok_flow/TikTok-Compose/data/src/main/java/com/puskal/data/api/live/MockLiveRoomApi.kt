package com.puskal.data.api.live

import com.puskal.data.model.VideoAsset
import com.puskal.data.model.live.LiveComment
import com.puskal.data.model.live.LiveProduct
import com.puskal.data.model.live.LiveRoomModel
import com.puskal.data.source.UsersDataSource.zoya
import com.puskal.data.source.VideoDataSource

class MockLiveRoomApi : LiveRoomApi {
    override suspend fun fetchLiveRoom(roomId: String): LiveRoomModel {
        val fileName = VideoDataSource.liveFileName(roomId)
        return LiveRoomModel(
            roomId = roomId,
            anchor = zoya,
            title = "本地直播间",
            liveStreamUrl = VideoAsset.liveAssetUri(fileName),
            onlineCount = 12876,
            statusTags = listOf("热门", "直播中", "本地流"),
            comments = listOf(
                LiveComment(id = "c1", userName = "小鹿", text = "主播今天状态太好了"),
                LiveComment(id = "c2", userName = "阿明", text = "这个推荐不错"),
                LiveComment(id = "c3", userName = "南希", text = "求上链接"),
                LiveComment(id = "c4", userName = "可可", text = "点赞了")
            ),
            currentProduct = LiveProduct(
                id = "p1",
                name = "夏季轻薄外套",
                priceText = "¥39.90"
            )
        )
    }
}
