package com.puskal.home.tab.foryou

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.puskal.composable.TikTokVerticalVideoPager
import com.puskal.composable.feed.rememberFeedScene
import com.puskal.core.DestinationRoute.COMMENT_BOTTOM_SHEET_ROUTE
import com.puskal.core.DestinationRoute.CREATOR_PROFILE_ROUTE
import com.puskal.core.DestinationRoute.LIVE_ROOM_ROUTE
import com.puskal.theme.DarkBlue
import com.puskal.theme.DarkPink

/**
 * Created by Puskal Khadka on 3/14/2023.
 */
@Composable
fun ForYouTabScreen(
    navController: NavController,
    viewModel: ForYouViewModel = hiltViewModel()
) {
    val viewState by viewModel.viewState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.horizontalGradient(
                    listOf(DarkPink, DarkBlue)
                )
            )
    ) {

        viewState?.forYouPageFeed?.let {
            val feedScene = rememberFeedScene(it)
            val lifecycleOwner = LocalLifecycleOwner.current

            LaunchedEffect(it) {
                feedScene.updateVideos(it)
            }

            DisposableEffect(lifecycleOwner, it) {
                val observer = LifecycleEventObserver { _, event ->
                    when (event) {
                        Lifecycle.Event.ON_START -> feedScene.onStart(it)
                        Lifecycle.Event.ON_STOP -> feedScene.onStop()
                        else -> Unit
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                    feedScene.onDispose()
                }
            }

            TikTokVerticalVideoPager(
                videos = it,
                feedScene = feedScene,
                onClickLivePreview = { roomId -> navController.navigate("$LIVE_ROOM_ROUTE/$roomId") },
                onclickComment = { videoId -> navController.navigate("$COMMENT_BOTTOM_SHEET_ROUTE/$videoId") },
                onClickLike = { s: String, b: Boolean -> },
                onclickFavourite = {},
                onClickAudio = {},
                onClickUser = { userId -> navController.navigate("$CREATOR_PROFILE_ROUTE/$userId") }
            )
        }
    }
}
