package com.puskal.data.source

import android.content.Context
import com.puskal.data.model.VideoAsset
import com.puskal.data.model.VideoModel
import com.puskal.data.source.UsersDataSource.charliePuth
import com.puskal.data.source.UsersDataSource.daniel
import com.puskal.data.source.UsersDataSource.duaLipa
import com.puskal.data.source.UsersDataSource.google
import com.puskal.data.source.UsersDataSource.imagineDragons
import com.puskal.data.source.UsersDataSource.jeremy
import com.puskal.data.source.UsersDataSource.kylieJenner
import com.puskal.data.source.UsersDataSource.shana
import com.puskal.data.source.UsersDataSource.taylor
import com.puskal.data.source.UsersDataSource.zoya
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object VideoDataSource {
    private val authors = listOf(
        kylieJenner,
        charliePuth,
        duaLipa,
        imagineDragons,
        taylor,
        zoya,
        google,
        daniel,
        shana,
        jeremy
    )

    private fun Context.videoFileNames(directory: String): List<String> {
        return runCatching {
            assets.list(directory)
                ?.filter { it.endsWith(".mp4", ignoreCase = true) }
                ?.sortedWith(
                    compareBy<String> { it.substringBeforeLast(".").toIntOrNull() ?: Int.MAX_VALUE }
                        .thenBy { it }
                )
                .orEmpty()
        }.getOrDefault(emptyList())
    }

    private fun Context.localVideoFileNames(): List<String> = videoFileNames(VideoAsset.DIRECTORY)

    private fun Context.liveVideoFileNames(): List<String> = videoFileNames(VideoAsset.LIVE_DIRECTORY)

    private fun Context.adVideoFileNames(): List<String> = videoFileNames(VideoAsset.AD_DIRECTORY)

    private fun buildLocalVideos(fileNames: List<String>): List<VideoModel> {
        return fileNames.mapIndexed { index, fileName ->
            val author = authors[index % authors.size]
            VideoModel(
                videoId = "local_video_${index + 1}",
                authorDetails = author,
                videoLink = fileName,
                videoStats = VideoModel.VideoStats(
                    like = 12000L + index * 973L,
                    comment = 300L + index * 31L,
                    share = 80L + index * 11L,
                    favourite = 240L + index * 17L
                ),
                description = "本地视频 ${index + 1} #推荐 #本地素材",
                audioModel = null,
                hasTag = listOf(),
                searchRecommendation = searchRecommendationFor(index)
            )
        }
    }

    private fun buildAdVideos(fileNames: List<String>, startIndex: Int): List<VideoModel> {
        return fileNames.mapIndexed { index, fileName ->
            val feedIndex = startIndex + index
            val author = authors[feedIndex % authors.size]
            val adId = fileName.substringBeforeLast(".")
            VideoModel(
                videoId = "ad_video_$adId",
                authorDetails = author,
                videoLink = fileName,
                assetDirectory = VideoAsset.AD_DIRECTORY,
                videoStats = VideoModel.VideoStats(
                    like = 12000L + feedIndex * 973L,
                    comment = 300L + feedIndex * 31L,
                    share = 80L + feedIndex * 11L,
                    favourite = 240L + feedIndex * 17L
                ),
                description = "\u672c\u5730\u5e7f\u544a\u89c6\u9891 ${index + 1} #\u63a8\u8350 #\u672c\u5730\u7d20\u6750",
                audioModel = null,
                hasTag = listOf(),
                isAdvertisement = true,
                actionLinkText = "\u67e5\u770b\u8be6\u60c5",
                actionLinkUrl = "tiktokflow://product/prod_1"
            )
        }
    }

    private fun buildLivePreviews(fileNames: List<String>): List<VideoModel> {
        return fileNames.mapIndexed { index, fileName ->
            val roomId = liveRoomId(fileName)
            VideoModel(
                videoId = "live_preview_${fileName.substringBeforeLast(".")}",
                authorDetails = zoya,
                videoLink = fileName,
                videoStats = VideoModel.VideoStats(
                    like = 98231L + index * 513L,
                    comment = 1243L + index * 23L,
                    share = 813L + index * 17L,
                    favourite = 561L + index * 13L
                ),
                description = "本地直播 ${index + 1}",
                livePreview = VideoModel.LivePreview(
                    roomId = roomId,
                    anchorName = zoya.fullName,
                    title = "本地直播 ${index + 1}",
                    onlineCount = 12876L + index * 317L,
                    liveStreamUrl = VideoAsset.liveAssetUri(fileName),
                    previewImageUrl = zoya.profilePic
                )
            )
        }
    }

    private fun searchRecommendationFor(index: Int): VideoModel.SearchRecommendation? {
        return when (index) {
            0 -> VideoModel.SearchRecommendation(
                searchText = "荣耀600",
                commentText = "点击了解全新荣耀600系列",
                label = "作者推荐",
                searchUrlKey = "search:荣耀600",
                authorRecommendUrlKey = "recommend:prod_1"
            )
            else -> null
        }
    }

    fun liveRoomId(fileName: String): String = "live_${fileName.substringBeforeLast(".")}"

    fun liveFileName(roomId: String): String = "${roomId.removePrefix("live_")}.mp4"

    fun fetchVideos(context: Context): Flow<List<VideoModel>> {
        return flow {
            val localVideos = buildLocalVideos(context.localVideoFileNames())
            val adVideos = buildAdVideos(context.adVideoFileNames(), localVideos.size)
            emit(localVideos + adVideos + buildLivePreviews(context.liveVideoFileNames()))
        }
    }

    fun fetchVideosOfParticularUser(context: Context, userId: Long): Flow<List<VideoModel>> {
        return flow {
            emit(buildLocalVideos(context.localVideoFileNames()).filter { it.authorDetails.userId == userId })
        }
    }

    fun findVideoById(context: Context, videoId: String): VideoModel? {
        return buildLocalVideos(context.localVideoFileNames()).firstOrNull { it.videoId == videoId }
    }

    private fun legacyVideo(videoId: String, authorIndex: Int = 0): VideoModel {
        return VideoModel(
            videoId = videoId,
            authorDetails = authors[authorIndex % authors.size],
            videoLink = "1.mp4",
            videoStats = VideoModel.VideoStats(
                like = 12000,
                comment = 300,
                share = 80,
                favourite = 240
            ),
            description = "本地视频 #推荐 #本地素材",
            audioModel = null,
            hasTag = listOf()
        )
    }

    object KylieJennerVideos {
        val kylie_vid1 = legacyVideo("kylie_vid1", 0)
        val kylieVideosList = listOf(kylie_vid1)
    }

    object CharliePuthVideos {
        val charlieputh_vid1 = legacyVideo("charlieputh_vid1", 1)
        val charliePuthVideos = arrayListOf(charlieputh_vid1)
    }

    object ImagineDragonsVideos {
        val imaginedragons_vid1 = legacyVideo("imaginedragons_vid1", 3)
        val imageDragonsVideosList = arrayListOf(imaginedragons_vid1)
    }

    object TaylorVideos {
        val taylor_vid2 = legacyVideo("taylor_vid2", 4)
        val taylorVideos = arrayListOf(taylor_vid2)
    }

    object DuaLipaVideos {
        val dua_vid1 = legacyVideo("dua_vid1", 2)
        val dualipaVideos = arrayListOf(dua_vid1)
    }

    object GoogleVideos {
        val google_vid1 = legacyVideo("google_vid1", 6)
        val googleVideosList = arrayListOf(google_vid1)
    }

    object ShanaVideos {
        val shana_vid1 = legacyVideo("shana_vid1", 8)
        val shanVideosList = arrayListOf(shana_vid1)
    }

    object JeremyVideos {
        val jeremy_vid1 = legacyVideo("jeremy_vid1", 9)
        val jeremeyVideosList = arrayListOf(jeremy_vid1)
    }

    object DanielVideos {
        val daniel_vid1 = legacyVideo("daniel_vid1", 7)
        val danielVideosList = arrayListOf(daniel_vid1)
    }

    object ZoyaVideos {
        val zoya_vid1 = legacyVideo("zoya_vid1", 5)
        val zoyaVideosList = arrayListOf(zoya_vid1)
    }
}
