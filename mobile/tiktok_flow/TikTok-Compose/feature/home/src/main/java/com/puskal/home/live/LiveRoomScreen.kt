package com.puskal.home.live

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.puskal.composable.live.SharedLivePlayerView
import com.puskal.data.model.live.LiveComment
import com.puskal.data.model.live.LiveProduct
import com.puskal.theme.R
import kotlinx.coroutines.delay

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LiveRoomScreen(
    navController: NavController,
    viewModel: LiveRoomViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val liveComments = remember(state.roomId) {
        mutableStateListOf<DisplayedLiveComment>()
    }
    var inputText by remember(state.roomId) { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(state.roomId, state.comments) {
        liveComments.clear()
        if (state.comments.isEmpty()) return@LaunchedEffect

        state.comments.take(LiveCommentVisibleCount).forEachIndexed { index, comment ->
            liveComments.add(
                DisplayedLiveComment(
                    displayId = "initial_${state.roomId}_$index",
                    comment = comment
                )
            )
        }

        var nextIndex = LiveCommentVisibleCount
        while (true) {
            delay(1_000)
            liveComments.add(
                DisplayedLiveComment(
                    displayId = "auto_${System.currentTimeMillis()}_$nextIndex",
                    comment = state.comments[nextIndex % state.comments.size]
                )
            )
            if (liveComments.size > LiveCommentVisibleCount) {
                liveComments.removeAt(0)
            }
            nextIndex++
        }
    }

    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        LiveRoomVideo(url = state.liveStreamUrl)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.32f),
                            Color.Transparent,
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.26f),
                            Color.Black.copy(alpha = 0.74f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 14.dp)
                .padding(bottom = 72.dp)
        ) {
            LiveRoomTopBar(
                state = state,
                onClose = { navController.popBackStack() }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                state.statusTags.forEach { tag ->
                    LiveCapsuleTag(text = tag)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            state.currentProduct?.let { product ->
                LiveProductFloatingCard(
                    product = product,
                    modifier = Modifier
                        .fillMaxWidth(0.62f)
                        .padding(bottom = 12.dp)
                )
            }

            LiveCommentStack(
                comments = liveComments,
                modifier = Modifier
                    .fillMaxWidth(0.84f)
                    .padding(bottom = 12.dp)
            )

        }

        LiveBottomInputBar(
            value = inputText,
            onValueChange = { inputText = it },
            onSend = {
                val content = inputText.trim()
                if (content.isNotEmpty()) {
                    liveComments.add(
                        DisplayedLiveComment(
                            displayId = "me_${System.currentTimeMillis()}",
                            comment = LiveComment(
                                id = "me_${System.currentTimeMillis()}",
                                userName = "我",
                                text = content
                            )
                        )
                    )
                    if (liveComments.size > LiveCommentVisibleCount) {
                        liveComments.removeAt(0)
                    }
                    inputText = ""
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = 12.dp, vertical = 14.dp)
        )
    }
}

@Composable
private fun LiveRoomTopBar(
    state: LiveRoomState,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = Color.Black.copy(alpha = 0.34f)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = state.anchor?.profilePic,
                    contentDescription = null,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, Color.White.copy(alpha = 0.7f), CircleShape),
                    contentScale = ContentScale.Crop
                )
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = state.anchor?.fullName.orEmpty(),
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${state.onlineCount}人正在观看",
                        color = Color.White.copy(alpha = 0.78f),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                LiveGradientButton(text = "关注")
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.Black.copy(alpha = 0.34f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LiveTopPill(text = "直播中", tint = MaterialTheme.colorScheme.primary)
                }
            }
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.34f)
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_cancel),
                        contentDescription = "关闭",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun LiveGradientButton(text: String) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.Transparent,
        modifier = Modifier.clip(RoundedCornerShape(18.dp))
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFFFFA24C), Color(0xFFFFD85C))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = text,
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LiveTopPill(text: String, tint: Color) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = tint
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun LiveCapsuleTag(text: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color.Black.copy(alpha = 0.34f)
    ) {
        Text(
            text = text,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun LiveProductFloatingCard(
    product: LiveProduct,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color.Black.copy(alpha = 0.46f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFFFC469), Color(0xFFFF7A59))
                        )
                    )
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "商品",
                    color = Color.White.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = product.name,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.priceText,
                    color = Color(0xFFFFC469),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.White.copy(alpha = 0.12f)
            ) {
                Text(
                    text = "查看",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LiveCommentStack(
    comments: List<DisplayedLiveComment>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = Color.Transparent
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            reverseLayout = false
        ) {
            items(
                items = comments,
                key = { it.displayId }
            ) { displayedComment ->
                LiveCommentBubble(
                    comment = displayedComment.comment,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LiveCommentBubble(
    comment: LiveComment,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color.Black.copy(alpha = 0.38f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF5E6FFF).copy(alpha = 0.72f)
            ) {
                Text(
                    text = ((comment.id.hashCode() and 0x7fffffff) % 9 + 10).toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                )
            }
            Text(
                text = "${comment.userName}: ${comment.text}",
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private data class DisplayedLiveComment(
    val displayId: String,
    val comment: LiveComment
)

private const val LiveCommentVisibleCount = 4

@Composable
private fun LiveBottomInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier
) {
    val canSend = value.trim().isNotEmpty()
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            color = Color.Black.copy(alpha = 0.44f)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                decorationBox = { innerTextField ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (value.isBlank()) {
                                Text(
                                    text = "说点什么...",
                                    color = Color.White.copy(alpha = 0.72f),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            innerTextField()
                        }
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(id = R.drawable.ic_emoji),
                            contentDescription = "表情",
                            tint = Color.White.copy(alpha = 0.88f)
                        )
                    }
                }
            )
        }

        Surface(
            shape = RoundedCornerShape(18.dp),
            color = if (canSend) Color(0xFFFF4FA3) else Color.White.copy(alpha = 0.14f),
            modifier = Modifier.clickable(enabled = canSend) { onSend() }
        ) {
            Text(
                text = "发送",
                color = if (canSend) Color.White else Color.White.copy(alpha = 0.52f),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
    }
}

@Composable
private fun LiveRoomVideo(url: String) {
    SharedLivePlayerView(
        url = url,
        modifier = Modifier.fillMaxSize(),
        isPlaying = true,
        volume = 1f
    )
}
