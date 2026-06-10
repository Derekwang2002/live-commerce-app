package com.puskal.data.source

import com.puskal.data.model.CommentList
import com.puskal.data.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.UUID

object CommentDataSource {
    private val comments = listOf(
        "太好看了，已经循环三遍",
        "这个镜头很稳",
        "本地素材效果不错",
        "求同款拍摄方式",
        "点赞收藏了",
        "这个节奏刚刚好",
        "评论区来报到",
        "画面质感很好",
        "继续更新",
        "这条应该上热门"
    )
    private val userNames = listOf("小鹿", "阿明", "可可", "南希", "老周", "星星", "一一", "小北")
    private val profilePic = "https://xsgames.co/randomusers/avatar.php"
    private val followers = 30L..450000L
    private val likesInComment = 0L..500L
    private val commentCache = mutableMapOf<String, CommentList>()

    fun fetchComment(videoId: String): Flow<CommentList> {
        return flow {
            emit(commentCache.getOrPut(videoId) { buildCommentList(videoId) })
        }
    }

    fun addComment(videoId: String, text: String): CommentList {
        val current = commentCache.getOrPut(videoId) { buildCommentList(videoId) }
        val newComment = CommentList.Comment(
            commentBy = UserModel(
                userId = 0,
                fullName = "我",
                uniqueUserName = "me",
                following = 0,
                followers = 0,
                likes = 0,
                bio = "",
                profilePic = "",
                isVerified = false
            ),
            comment = text.trim(),
            createdAt = "刚刚",
            totalLike = 0,
            totalDisLike = 0,
            threadCount = 0,
            thread = emptyList()
        )
        return current.copy(
            totalComment = current.totalComment + 1,
            comments = listOf(newComment) + current.comments
        ).also {
            commentCache[videoId] = it
        }
    }

    private fun buildCommentList(videoId: String): CommentList {
        val visibleComments = comments.shuffled().take((6..comments.size).random())
        return CommentList(
            videoId = videoId,
            totalComment = visibleComments.size,
            comments = visibleComments.map {
                val userName = userNames.random()
                CommentList.Comment(
                    commentBy = UserModel(
                        userId = System.currentTimeMillis(),
                        fullName = userName,
                        uniqueUserName = userName,
                        following = followers.random(),
                        followers = followers.random(),
                        likes = followers.random(),
                        bio = "暂无简介",
                        profilePic = "$profilePic?g=female&id=${UUID.randomUUID().toString().take(4)}",
                        isVerified = listOf(true, false).random()
                    ),
                    comment = it,
                    createdAt = "${(1..23).random()}小时前",
                    totalLike = likesInComment.random(),
                    totalDisLike = 0,
                    threadCount = 0,
                    thread = emptyList()
                )
            },
            isCommentPrivate = false
        )
    }
}
