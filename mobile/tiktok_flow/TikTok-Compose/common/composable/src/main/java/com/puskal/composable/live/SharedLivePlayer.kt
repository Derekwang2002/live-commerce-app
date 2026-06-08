package com.puskal.composable.live

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

object SharedLivePlayerStore {
    private val players = mutableMapOf<String, ExoPlayer>()
    private val activeSessions = mutableMapOf<String, Any>()

    fun getOrCreate(context: Context, url: String): ExoPlayer {
        return players.getOrPut(url) {
            ExoPlayer.Builder(context.applicationContext).build().apply {
                repeatMode = Player.REPEAT_MODE_ONE
                setMediaItem(MediaItem.fromUri(Uri.parse(url)))
                prepare()
            }
        }
    }

    fun pauseAllExcept(url: String) {
        players.forEach { (playerUrl, player) ->
            if (playerUrl != url) {
                player.volume = 0f
                player.playWhenReady = false
                player.pause()
                activeSessions.remove(playerUrl)
            }
        }
    }

    fun play(url: String, session: Any, volume: Float) {
        val player = players[url] ?: return
        pauseAllExcept(url)
        activeSessions[url] = session
        player.volume = volume
        player.playWhenReady = true
        player.play()
    }

    fun pause(url: String, session: Any) {
        if (activeSessions[url] != session) return
        players[url]?.let { player ->
            player.volume = 0f
            player.playWhenReady = false
            player.pause()
        }
        activeSessions.remove(url)
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun SharedLivePlayerView(
    url: String,
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    volume: Float
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val player = remember(url) {
        SharedLivePlayerStore.getOrCreate(context, url)
    }
    val session = remember(url) { Any() }
    val playerView = remember(player) {
        PlayerView(context).apply {
            this.player = player
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    LaunchedEffect(player, url, isPlaying, volume) {
        if (isPlaying) {
            SharedLivePlayerStore.play(url, session, volume)
        } else {
            SharedLivePlayerStore.pause(url, session)
        }
    }

    DisposableEffect(playerView, url, session) {
        onDispose {
            SharedLivePlayerStore.pause(url, session)
            playerView.player = null
        }
    }

    AndroidView(
        factory = { playerView },
        update = {
            if (it.player !== player) {
                it.player = player
            }
        },
        modifier = modifier
    )
}
