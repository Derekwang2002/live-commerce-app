package com.puskal.composable


import android.content.Intent
import android.graphics.Paint as AndroidPaint
import android.graphics.Path as AndroidPath
import android.graphics.PathMeasure as AndroidPathMeasure
import android.net.Uri
import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.puskal.core.extension.formattedCount
import com.puskal.core.extension.Space
import com.puskal.core.utils.IntentUtils.share
import com.puskal.composable.feed.FeedScene
import com.puskal.composable.live.SharedLivePlayerView
import com.puskal.data.model.VideoModel
import com.puskal.theme.*
import com.puskal.theme.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import androidx.compose.runtime.snapshotFlow

/**
 * Created by Puskal Khadka on 3/16/2023.
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun TikTokVerticalVideoPager(
    modifier: Modifier = Modifier,
    videos: List<VideoModel>,
    initialPage: Int? = 0,
    showUploadDate: Boolean = false,
    onclickComment: (videoId: String) -> Unit,
    onClickLike: (videoId: String, likeStatus: Boolean) -> Unit,
    onclickFavourite: (videoId: String) -> Unit,
    onClickAudio: (VideoModel) -> Unit,
    onClickUser: (userId: Long) -> Unit,
    onClickLivePreview: (roomId: String) -> Unit = {},
    feedScene: FeedScene? = null,
    onClickFavourite: (isFav: Boolean) -> Unit = {},
    onClickShare: (() -> Unit)? = null
) {
    val pagerState = rememberPagerState(initialPage = initialPage ?: 0)
    val coroutineScope = rememberCoroutineScope()
    val localDensity = LocalDensity.current

    val fling = PagerDefaults.flingBehavior(
        state = pagerState, lowVelocityAnimationSpec = tween(
            easing = LinearEasing, durationMillis = 300
        )
    )

    LaunchedEffect(pagerState, videos, feedScene) {
        if (feedScene == null) return@LaunchedEffect
        snapshotFlow { pagerState.settledPage }
            .map { it.coerceIn(0, (videos.size - 1).coerceAtLeast(0)) }
            .filter { videos.isNotEmpty() }
            .distinctUntilChanged()
            .collectLatest { page ->
                feedScene.onPageSettled(page, videos)
            }
    }

    VerticalPager(
        pageCount = videos.size,
        state = pagerState,
        flingBehavior = fling,
        beyondBoundsPageCount = 1,
        modifier = modifier
    ) {
        var pauseButtonVisibility by remember { mutableStateOf(false) }
        var doubleTapState by remember {
            mutableStateOf(
                Triple(
                    Offset.Unspecified, //offset
                    false, //double tap anim start
                    0f //rotation angle
                )
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            if (videos[it].livePreview != null) {
                LivePreviewCard(
                    item = videos[it],
                    isActive = pagerState.currentPage == it,
                    onClickLivePreview = onClickLivePreview
                )
            } else {
                VideoPlayer(video = videos[it], pagerState = pagerState, pageIndex = it, player = feedScene?.getPlayer(it), onSingleTap = { player ->
                    if (feedScene != null) {
                        val isPlaying = feedScene.onVideoTapped(it)
                        pauseButtonVisibility = !isPlaying
                    } else {
                        pauseButtonVisibility = player.isPlaying
                        player.playWhenReady = !player.isPlaying
                    }
                },
                    onDoubleTap = { exoPlayer, offset ->
                        coroutineScope.launch {
                            videos[it].currentViewerInteraction.isLikedByYou = true
                            val rotationAngle = (-10..10).random()
                            doubleTapState = Triple(offset, true, rotationAngle.toFloat())
                            delay(400)
                            doubleTapState = Triple(offset, false, rotationAngle.toFloat())
                        }
                    },
                    onVideoDispose = { pauseButtonVisibility = false },
                    onVideoGoBackground = { pauseButtonVisibility = false }

                )
            }

            if (videos[it].livePreview == null) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = FeedActionOverlayBottomPadding)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.Bottom,
                    ) {
                        FooterUi(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            item = videos[it],
                            showUploadDate=showUploadDate,
                            onClickAudio = onClickAudio,
                            onClickUser = onClickUser,
                        )

                        SideItems(
                            modifier = Modifier,
                            videos[it],
                            doubleTabState = doubleTapState,
                            onclickComment = onclickComment,
                            onClickUser = onClickUser,
                            onClickFavourite = onClickFavourite,
                            onClickShare = onClickShare
                        )
                    }
                    if (
                        videos[it].isAdvertisement &&
                        videos[it].actionLinkText != null &&
                        videos[it].actionLinkUrl != null
                    ) {
                        10.dp.Space()
                        AdActionLink(
                            text = videos[it].actionLinkText.orEmpty(),
                            url = videos[it].actionLinkUrl.orEmpty(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 12.dp, end = 96.dp)
                        )
                    }
                }
            }


            AnimatedVisibility(
                visible = pauseButtonVisibility,
                enter = scaleIn(spring(Spring.DampingRatioMediumBouncy), initialScale = 1.5f),
                exit = scaleOut(tween(150)),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_play),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(36.dp)
                )
            }

            val iconSize = 110.dp
            AnimatedVisibility(visible = doubleTapState.second,
                enter = scaleIn(spring(Spring.DampingRatioMediumBouncy), initialScale = 1.3f),
                exit = scaleOut(
                    tween(600), targetScale = 1.58f
                ) + fadeOut(tween(600)) + slideOutVertically(
                    tween(600)
                ),
                modifier = Modifier.run {
                    if (doubleTapState.first != Offset.Unspecified) {
                        this.offset(x = localDensity.run {
                            doubleTapState.first.x.toInt().toDp().plus(-iconSize.div(2))
                        }, y = localDensity.run {
                            doubleTapState.first.y.toInt().toDp().plus(-iconSize.div(2))
                        })
                    } else this
                }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_like),
                    contentDescription = null,
                    tint = if (doubleTapState.second) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(
                        alpha = 0.8f
                    ),
                    modifier = Modifier
                        .size(iconSize)
                        .rotate(doubleTapState.third)
                )
            }


        }
    }

}

private val FeedActionOverlayBottomPadding = 32.dp
private const val LivePreviewAutoEnterMillis = 15_000L
private const val LivePreviewCountdownMillis = 10_000L
private const val LivePreviewCountdownStepMillis = 100L

@Composable
private fun LivePreviewCard(
    item: VideoModel,
    isActive: Boolean,
    onClickLivePreview: (roomId: String) -> Unit
) {
    val preview = item.livePreview ?: return
    var elapsedMillis by remember(preview.roomId) { mutableStateOf(0L) }
    var hasEnteredLiveRoom by remember(preview.roomId) { mutableStateOf(false) }
    val countdownMillis = (LivePreviewAutoEnterMillis - elapsedMillis)
        .coerceIn(0L, LivePreviewCountdownMillis)
    val countdownProgress = if (elapsedMillis >= LivePreviewAutoEnterMillis - LivePreviewCountdownMillis) {
        1f - countdownMillis.toFloat() / LivePreviewCountdownMillis
    } else {
        0f
    }
    val enterButtonText = if (countdownProgress > 0f) {
        "\u9a6c\u4e0a\u8fdb\u5165\u76f4\u64ad\u95f4"
    } else {
        "\u70b9\u51fb\u8fdb\u5165\u76f4\u64ad\u95f4"
    }
    val enterLiveRoom = {
        if (!hasEnteredLiveRoom) {
            hasEnteredLiveRoom = true
            onClickLivePreview(preview.roomId)
        }
    }

    LaunchedEffect(isActive, preview.roomId) {
        elapsedMillis = 0L
        hasEnteredLiveRoom = false
        if (!isActive) return@LaunchedEffect

        while (elapsedMillis < LivePreviewAutoEnterMillis && !hasEnteredLiveRoom) {
            delay(LivePreviewCountdownStepMillis)
            elapsedMillis = (elapsedMillis + LivePreviewCountdownStepMillis)
                .coerceAtMost(LivePreviewAutoEnterMillis)
        }
        if (!hasEnteredLiveRoom) {
            enterLiveRoom()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { enterLiveRoom() }
    ) {
        LivePreviewMedia(
            item = item,
            modifier = Modifier.fillMaxSize(),
            volume = if (isActive) 1f else 0f
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.40f),
                            Color.Black.copy(alpha = 0.24f),
                            Color.Black.copy(alpha = 0.28f),
                            Color.Black.copy(alpha = 0.70f)
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fillMaxWidth()
                .padding(horizontal = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1.95f))

            Box(
                modifier = Modifier.livePreviewCountdownBorder(
                    visible = countdownProgress > 0f,
                    progress = countdownProgress
                )
            ) {
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color.White.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.42f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_playback),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = enterButtonText,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            }

            Spacer(modifier = Modifier.weight(0.25f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "直播中",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
                Surface(
                    color = Color.Black.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "主播第1名",
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = item.authorDetails.profilePic,
                    contentDescription = null,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color.White.copy(alpha = 0.72f), CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "@${item.authorDetails.uniqueUserName}",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = preview.title,
                        color = Color.White.copy(alpha = 0.96f),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { enterLiveRoom() }
        )
    }
}

private fun Modifier.livePreviewCountdownBorder(
    visible: Boolean,
    progress: Float
): Modifier = this
    .drawWithContent {
        drawContent()
        if (!visible) return@drawWithContent

        val strokeWidth = 3.dp.toPx()
        val inset = strokeWidth / 2f
        val left = inset
        val top = inset
        val right = size.width - inset
        val bottom = size.height - inset
        val radius = (28.dp.toPx() - inset).coerceAtLeast(0f)
        val path = AndroidPath().apply {
            moveTo(left + radius, top)
            lineTo(right - radius, top)
            quadTo(right, top, right, top + radius)
            lineTo(right, bottom - radius)
            quadTo(right, bottom, right - radius, bottom)
            lineTo(left + radius, bottom)
            quadTo(left, bottom, left, bottom - radius)
            lineTo(left, top + radius)
            quadTo(left, top, left + radius, top)
            close()
        }
        val pathMeasure = AndroidPathMeasure(path, false)
        val segment = AndroidPath()
        pathMeasure.getSegment(
            0f,
            pathMeasure.length * progress.coerceIn(0f, 1f),
            segment,
            true
        )
        val paint = AndroidPaint(AndroidPaint.ANTI_ALIAS_FLAG).apply {
            color = android.graphics.Color.WHITE
            style = AndroidPaint.Style.STROKE
            this.strokeWidth = strokeWidth
            strokeCap = AndroidPaint.Cap.ROUND
            strokeJoin = AndroidPaint.Join.ROUND
        }
        drawContext.canvas.nativeCanvas.drawPath(segment, paint)
    }

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
private fun LivePreviewMedia(
    item: VideoModel,
    modifier: Modifier = Modifier,
    volume: Float = 1f
) {
    val preview = item.livePreview
    val videoUrl = preview?.liveStreamUrl
    if (videoUrl != null) {
        SharedLivePlayerView(
            url = videoUrl,
            modifier = modifier,
            isPlaying = volume > 0f,
            volume = volume
        )
    } else {
        AsyncImage(
            model = preview?.previewImageUrl ?: item.authorDetails.profilePic,
            contentDescription = null,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}


@Composable
fun SideItems(
    modifier: Modifier,
    item: VideoModel,
    doubleTabState: Triple<Offset, Boolean, Float>,
    onclickComment: (videoId: String) -> Unit,
    onClickUser: (userId: Long) -> Unit,
    onClickShare: (() -> Unit)? = null,
    onClickFavourite: (isFav: Boolean) -> Unit
) {

    val context = LocalContext.current
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = item.authorDetails.profilePic,
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .border(
                    BorderStroke(width = 1.dp, color = White), shape = CircleShape
                )
                .clip(shape = CircleShape)
                .clickable {
                    onClickUser.invoke(item.authorDetails.userId)
                },
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(id = R.drawable.ic_plus),
            contentDescription = null,
            modifier = Modifier
                .offset(y = (-10).dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colorScheme.primary)
                .padding(5.5.dp),
            colorFilter = ColorFilter.tint(Color.White)
        )

        12.dp.Space()

        var isLiked by remember {
            mutableStateOf(item.currentViewerInteraction.isLikedByYou)
        }

        LaunchedEffect(key1 = doubleTabState) {
            if (doubleTabState.first != Offset.Unspecified && doubleTabState.second) {
                isLiked = doubleTabState.second
            }
        }
        LikeIconButton(isLiked = isLiked,
            likeCount = item.videoStats.formattedLikeCount,
            onLikedClicked = {
                isLiked = it
                item.currentViewerInteraction.isLikedByYou = it
            })


        Icon(painter = painterResource(id = R.drawable.ic_comment),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(33.dp)
                .clickable {
                    onclickComment(item.videoId)
                })
        Text(
            text = item.videoStats.formattedCommentCount,
            style = MaterialTheme.typography.labelMedium
        )
        16.dp.Space()



        var isFavourite by remember {
            mutableStateOf(item.currentViewerInteraction.isAddedToFavourite)
        }
        var favouriteCount by remember {
            mutableStateOf(item.videoStats.favourite)
        }

        FavouriteIconButton(
            isFavourite = isFavourite,
            favouriteCount = favouriteCount.formattedCount(),
            onFavouriteClicked = { selected ->
                if (selected != isFavourite) {
                    favouriteCount = (favouriteCount + if (selected) 1 else -1).coerceAtLeast(0)
                }
                isFavourite = selected
                item.currentViewerInteraction.isAddedToFavourite = selected
                item.videoStats.favourite = favouriteCount
                item.videoStats.formattedFavouriteCount = favouriteCount.formattedCount()
                onClickFavourite(selected)
            }
        )

        Icon(
            painter = painterResource(id = R.drawable.ic_share),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .size(32.dp)
                .clickable {
                    onClickShare?.let { onClickShare.invoke() } ?: run {
                        context.share(text = "分享这个本地视频")
                    }
                }
        )
        Text(
            text = item.videoStats.formattedShareCount, style = MaterialTheme.typography.labelMedium
        )
        20.dp.Space()

        RotatingAudioView(item.authorDetails.profilePic)

    }
}

@Composable
fun LikeIconButton(
    isLiked: Boolean, likeCount: String, onLikedClicked: (Boolean) -> Unit
) {

    val maxSize = 38.dp
    val iconSize by animateDpAsState(targetValue = if (isLiked) 33.dp else 32.dp,
        animationSpec = keyframes {
            durationMillis = 400
            24.dp.at(50)
            maxSize.at(190)
            26.dp.at(330)
            32.dp.at(400).with(FastOutLinearInEasing)
        })

    Box(
        modifier = Modifier
            .size(maxSize)
            .clickable(interactionSource = MutableInteractionSource(), indication = null) {
                onLikedClicked(!isLiked)
            }, contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_heart),
            contentDescription = null,
            tint = if (isLiked) MaterialTheme.colorScheme.primary else Color.White,
            modifier = Modifier.size(iconSize)
        )
    }

    Text(text = likeCount, style = MaterialTheme.typography.labelMedium)
    16.dp.Space()
}

@Composable
fun FavouriteIconButton(
    isFavourite: Boolean,
    favouriteCount: String,
    onFavouriteClicked: (Boolean) -> Unit
) {
    val maxSize = 38.dp
    val iconSize by animateDpAsState(targetValue = if (isFavourite) 33.dp else 32.dp,
        animationSpec = keyframes {
            durationMillis = 400
            24.dp.at(50)
            maxSize.at(190)
            26.dp.at(330)
            32.dp.at(400).with(FastOutLinearInEasing)
        })

    Box(
        modifier = Modifier
            .size(maxSize)
            .clickable(interactionSource = MutableInteractionSource(), indication = null) {
                onFavouriteClicked(!isFavourite)
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_bookmark),
            contentDescription = null,
            tint = if (isFavourite) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.86f),
            modifier = Modifier.size(iconSize)
        )
    }

    Text(text = favouriteCount, style = MaterialTheme.typography.labelMedium)
    14.dp.Space()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FooterUi(
    modifier: Modifier,
    item: VideoModel,
    showUploadDate: Boolean,
    onClickAudio: (VideoModel) -> Unit,
    onClickUser: (userId: Long) -> Unit,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.Bottom) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
            onClickUser(item.authorDetails.userId)
        }) {
            Text(
                text = item.authorDetails.fullName, style = MaterialTheme.typography.bodyMedium
            )
            if (showUploadDate) {
                Text(
                    text = " · ${item.createdAt}前",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
        5.dp.Space()
        Text(
            text = item.description,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.fillMaxWidth(0.85f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (item.isAdvertisement) {
            6.dp.Space()
            AdDisclosureTag(text = item.adLabel)
        }
        8.dp.Space()
        val audioInfo: String = item.audioModel?.run {
            "原声 - ${audioAuthor.uniqueUserName} - ${audioAuthor.fullName}"
        }
            ?: item.run { "原声 - ${item.authorDetails.uniqueUserName} - ${item.authorDetails.fullName}" }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clickable {
                    onClickAudio(item)
                }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_music_note),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = audioInfo,
                color = Color.White,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Clip,
                modifier = Modifier
                    .weight(1f)
                    .basicMarquee()
            )
        }
    }
}


@Composable
private fun AdDisclosureTag(text: String) {
    Surface(
        color = Color.Black.copy(alpha = 0.36f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.82f),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun AdActionLink(
    text: String,
    url: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        Color(0xFFFFD166),
                        Color(0xFFFF7A5C),
                        Color(0xFFFF5B5F)
                    )
                )
            )
            .clickable {
                runCatching {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }.onFailure {
                    Log.e("AdActionLink", "open ad url failed: ${it.message}")
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$text >",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun RotatingAudioView(img: String) {
    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = keyframes { durationMillis = 7000 })
    )

    Box(modifier = Modifier.rotate(angle)) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            Gray20, Gray20, GrayLight, Gray20, Gray20,
                        )
                    ), shape = CircleShape
                )
                .size(46.dp), contentAlignment = Alignment.Center
        ) {

            AsyncImage(
                model = img,
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

        }
    }

}
