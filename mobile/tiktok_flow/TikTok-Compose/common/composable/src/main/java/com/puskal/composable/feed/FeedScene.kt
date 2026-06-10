package com.puskal.composable.feed

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.puskal.data.model.VideoModel

@Stable
class FeedScene internal constructor(
    context: Context,
    videos: List<VideoModel>
) {
    private val playerManager = FeedPlayerManager(context, videos)
    private val preloadManager = FeedPreloadManager()

    var currentIndex: Int = 0
        private set

    private var hasStarted = false

    fun updateVideos(videos: List<VideoModel>) {
        playerManager.updateVideos(videos)
        if (videos.isEmpty()) return
        if (currentIndex !in videos.indices) {
            currentIndex = 0
        }
        if (hasStarted) {
            playerManager.play(currentIndex)
            preloadManager.onCurrentIndexChanged(currentIndex, videos)
        }
    }

    fun getPlayer(index: Int) = playerManager.getOrCreate(index)

    fun onPageSettled(newIndex: Int, videos: List<VideoModel>) {
        if (videos.isEmpty() || newIndex !in videos.indices) return
        val previous = currentIndex
        currentIndex = newIndex
        if (previous != newIndex) {
            playerManager.pause(previous)
        }
        playerManager.play(newIndex)
        preloadManager.onCurrentIndexChanged(newIndex, videos)
        playerManager.releaseFarAwayPlayers(newIndex, keepDistance = 1)
    }

    fun onVideoTapped(index: Int): Boolean {
        if (index != currentIndex) return false
        return playerManager.toggle(index)
    }

    fun onStart(videos: List<VideoModel>) {
        hasStarted = true
        if (videos.isEmpty()) return
        playerManager.play(currentIndex)
        preloadManager.onCurrentIndexChanged(currentIndex, videos)
    }

    fun onStop() {
        hasStarted = false
        playerManager.onStop()
    }

    fun onDispose() {
        preloadManager.release()
        playerManager.releaseAll()
    }
}

@Composable
fun rememberFeedScene(videos: List<VideoModel>): FeedScene {
    val context = LocalContext.current
    return remember(context) { FeedScene(context, videos) }
}
