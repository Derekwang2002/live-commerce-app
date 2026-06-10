package com.puskal.composable.feed

import android.content.Context
import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.puskal.data.model.VideoAsset
import com.puskal.data.model.VideoModel
import kotlin.math.abs

class FeedPlayerManager(
    private val context: Context,
    private var videos: List<VideoModel>
) {
    private val players = mutableMapOf<Int, ExoPlayer>()
    private var activeIndex: Int? = null

    fun updateVideos(newVideos: List<VideoModel>) {
        videos = newVideos
        val validIndexes = videos.indices.toSet()
        val toRelease = players.keys.filterNot { it in validIndexes }
        toRelease.forEach { index ->
            players.remove(index)?.release()
        }
        if (activeIndex !in validIndexes) {
            activeIndex = null
        }
    }

    fun getOrCreate(index: Int): ExoPlayer? {
        if (index !in videos.indices) return null
        if (videos[index].livePreview != null) return null
        return players.getOrPut(index) {
            val video = videos[index]
            ExoPlayer.Builder(context).build().apply {
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                repeatMode = Player.REPEAT_MODE_ONE
                setMediaItem(
                    MediaItem.fromUri(
                        Uri.parse(VideoAsset.assetUri(video.videoLink, video.assetDirectory))
                    )
                )
                prepare()
            }
        }
    }

    fun play(index: Int) {
        val player = getOrCreate(index) ?: return
        pauseAllExcept(index)
        player.playWhenReady = true
        player.play()
        activeIndex = index
    }

    fun pause(index: Int) {
        players[index]?.let {
            it.playWhenReady = false
            it.pause()
        }
        if (activeIndex == index) activeIndex = null
    }

    fun toggle(index: Int): Boolean {
        val player = getOrCreate(index) ?: return false
        val shouldPlay = !player.isPlaying
        if (shouldPlay) {
            play(index)
        } else {
            pause(index)
        }
        return shouldPlay
    }

    fun pauseAllExcept(index: Int) {
        players.forEach { (i, player) ->
            if (i != index) {
                player.playWhenReady = false
                player.pause()
            }
        }
    }

    fun onStop() {
        activeIndex?.let { index ->
            players[index]?.let {
                it.playWhenReady = false
                it.pause()
            }
        }
    }

    fun onStart() {
        activeIndex?.let { play(it) }
    }

    fun releaseFarAwayPlayers(currentIndex: Int, keepDistance: Int = 1) {
        val toRelease = players.keys.filter { abs(it - currentIndex) > keepDistance }
        toRelease.forEach { index ->
            players.remove(index)?.release()
            if (activeIndex == index) activeIndex = null
        }
    }

    fun releaseAll() {
        players.values.forEach { it.release() }
        players.clear()
        activeIndex = null
    }
}
