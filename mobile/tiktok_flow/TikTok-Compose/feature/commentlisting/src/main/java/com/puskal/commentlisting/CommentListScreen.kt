package com.puskal.commentlisting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.puskal.core.extension.Space
import com.puskal.data.model.CommentList
import com.puskal.data.model.VideoModel
import com.puskal.theme.DarkBlue
import com.puskal.theme.GrayMainColor
import com.puskal.theme.R
import com.puskal.theme.SubTextColor

private const val RecommendationCommentLikeCount = 392L

private val CommentLikeColor = Color(0xFFFE2C55)

private enum class CommentReaction {
    None,
    Liked,
    Disliked
}

private fun CommentList.Comment.commentReactionKey(): String {
    return "${commentBy.userId}|$createdAt|${comment.orEmpty()}"
}

@Composable
fun CommentListScreen(
    viewModel: CommentListViewModel = hiltViewModel(),
    onClickCancel: () -> Unit
) {
    val viewState by viewModel.viewState.collectAsState()
    val recommendation = viewState?.searchRecommendation

    Column(
        modifier = Modifier.fillMaxHeight(0.75f)
    ) {
        12.dp.Space()
        CommentHeader(
            recommendation = recommendation,
            onClickCancel = onClickCancel
        )

        6.dp.Space()
        LazyColumn(contentPadding = PaddingValues(top = 4.dp), modifier = Modifier.weight(1f)) {
            recommendation?.let {
                item {
                    SearchRecommendationComment(it)
                }
            }
            viewState?.comments?.comments?.let { comments ->
                items(
                    items = comments,
                    key = { comment -> comment.commentReactionKey() }
                ) { comment ->
                    CommentItem(comment)
                }
            }
        }

        CommentUserField(
            onSubmitComment = {
                viewModel.onTriggerEvent(CommentEvent.SubmitComment(it))
            }
        )
    }
}

@Composable
private fun CommentHeader(
    recommendation: VideoModel.SearchRecommendation?,
    onClickCancel: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(32.dp)
            .padding(horizontal = 16.dp)
    ) {
        recommendation?.let {
            Text(
                text = buildAnnotatedString {
                    append("大家都在搜：")
                    withStyle(SpanStyle(color = Color(0xFF0068B7))) {
                        append(it.searchText)
                    }
                },
                style = MaterialTheme.typography.bodySmall,
                color = SubTextColor,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
        Icon(
            painter = painterResource(id = R.drawable.ic_cancel),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clickable { onClickCancel() }
        )
    }
}

@Composable
private fun CommentLikeButton(
    isLiked: Boolean,
    likeCount: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            painter = painterResource(
                id = if (isLiked) R.drawable.ic_like else R.drawable.ic_like_outline
            ),
            contentDescription = null,
            tint = if (isLiked) CommentLikeColor else Color.Unspecified,
            modifier = Modifier.size(18.dp)
        )
        likeCount.takeIf { it != 0L }?.let {
            Text(
                text = it.toString(),
                fontSize = 13.sp,
                color = if (isLiked) CommentLikeColor else SubTextColor
            )
        }
    }
}

@Composable
private fun CommentDislikeButton(
    isDisliked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(
                id = if (isDisliked) R.drawable.ic_dislike else R.drawable.ic_dislike_outline
            ),
            contentDescription = null,
            tint = if (isDisliked) SubTextColor else Color.Unspecified,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun SearchRecommendationComment(recommendation: VideoModel.SearchRecommendation) {
    var reaction by rememberSaveable(recommendation.commentText, recommendation.label) {
        mutableStateOf(CommentReaction.None)
    }
    val isLiked = reaction == CommentReaction.Liked
    val isDisliked = reaction == CommentReaction.Disliked
    val likeCount = RecommendationCommentLikeCount + if (isLiked) 1L else 0L

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        val (profileImg, name, badge, comment, link, createdOn, like, dislike) = createRefs()

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data("https://xsgames.co/randomusers/avatar.php?g=female&id=alisha")
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(GrayMainColor)
                .constrainAs(profileImg) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
        )

        Text(
            text = "Alisha",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.constrainAs(name) {
                start.linkTo(profileImg.end, margin = 12.dp)
                top.linkTo(profileImg.top)
            }
        )

        Text(
            text = "作者",
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(3.dp))
                .padding(horizontal = 5.dp, vertical = 2.dp)
                .constrainAs(badge) {
                    start.linkTo(name.end, margin = 6.dp)
                    centerVerticallyTo(name)
                }
        )

        Text(
            text = recommendation.commentText,
            style = MaterialTheme.typography.bodySmall,
            color = DarkBlue,
            modifier = Modifier.constrainAs(comment) {
                start.linkTo(name.start)
                top.linkTo(name.bottom, margin = 5.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = recommendation.label,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF0068B7),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.constrainAs(link) {
                start.linkTo(comment.start)
                top.linkTo(comment.bottom, margin = 5.dp)
            }
        )

        Text(
            text = "7小时前",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.constrainAs(createdOn) {
                start.linkTo(link.start)
                top.linkTo(link.bottom, margin = 5.dp)
            }
        )

        CommentLikeButton(
            isLiked = isLiked,
            likeCount = likeCount,
            onClick = {
                reaction = if (isLiked) CommentReaction.None else CommentReaction.Liked
            },
            modifier = Modifier.constrainAs(like) {
                bottom.linkTo(createdOn.bottom)
                end.linkTo(dislike.start, margin = 24.dp)
            }
        )

        CommentDislikeButton(
            isDisliked = isDisliked,
            onClick = {
                reaction = if (isDisliked) CommentReaction.None else CommentReaction.Disliked
            },
            modifier = Modifier.constrainAs(dislike) {
                bottom.linkTo(createdOn.bottom)
                end.linkTo(parent.end)
            }
        )
    }
    24.dp.Space()
}

@Composable
fun CommentItem(item: CommentList.Comment) {
    var reaction by rememberSaveable(item.commentReactionKey()) {
        mutableStateOf(CommentReaction.None)
    }
    val isLiked = reaction == CommentReaction.Liked
    val isDisliked = reaction == CommentReaction.Disliked
    val likeCount = item.totalLike + if (isLiked) 1L else 0L

    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        val (profileImg, name, comment, createdOn, reply, like, dislike) = createRefs()

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.commentBy.profilePic)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(GrayMainColor)
                .constrainAs(profileImg) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                }
        )

        Text(
            text = item.commentBy.fullName,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.constrainAs(name) {
                start.linkTo(profileImg.end, margin = 12.dp)
                top.linkTo(profileImg.top)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
        Text(
            text = item.comment ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = DarkBlue,
            modifier = Modifier.constrainAs(comment) {
                start.linkTo(name.start)
                top.linkTo(name.bottom, margin = 5.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
        Text(
            text = item.createdAt,
            modifier = Modifier.constrainAs(createdOn) {
                start.linkTo(name.start)
                top.linkTo(comment.bottom, margin = 5.dp)
            }
        )

        Text(
            text = stringResource(id = R.string.reply),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.constrainAs(reply) {
                start.linkTo(createdOn.end, margin = 16.dp)
                top.linkTo(createdOn.top)
                end.linkTo(like.end, margin = 4.dp)
                width = Dimension.fillToConstraints
            }
        )

        CommentLikeButton(
            isLiked = isLiked,
            likeCount = likeCount,
            onClick = {
                reaction = if (isLiked) CommentReaction.None else CommentReaction.Liked
            },
            modifier = Modifier.constrainAs(like) {
                bottom.linkTo(reply.bottom)
                end.linkTo(dislike.start, margin = 24.dp)
            }
        )

        CommentDislikeButton(
            isDisliked = isDisliked,
            onClick = {
                reaction = if (isDisliked) CommentReaction.None else CommentReaction.Disliked
            },
            modifier = Modifier.constrainAs(dislike) {
                bottom.linkTo(reply.bottom)
                end.linkTo(parent.end)
            }
        )
    }
    24.dp.Space()
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CommentUserField(
    onSubmitComment: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val canSend = commentText.trim().isNotEmpty()
    val submitComment = {
        val text = commentText.trim()
        if (text.isNotEmpty()) {
            onSubmitComment(text)
            commentText = ""
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    }

    Column(
        modifier = Modifier
            .shadow(elevation = (0.4).dp)
            .padding(horizontal = 16.dp)
            .imePadding()
    ) {
        HighlightedEmoji.values().toList().let {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                it.forEach { emoji ->
                    Text(text = emoji.unicode, fontSize = 25.sp)
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                shape = RoundedCornerShape(36.dp),
                placeholder = {
                    Text(
                        text = stringResource(R.string.add_comment),
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    containerColor = GrayMainColor,
                    unfocusedBorderColor = Color.Transparent
                ),
                modifier = Modifier
                    .height(46.dp)
                    .weight(1f),
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = DarkBlue),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    submitComment()
                }),
                trailingIcon = {
                    TextButton(
                        onClick = submitComment,
                        enabled = canSend,
                        contentPadding = PaddingValues(horizontal = 12.dp)
                    ) {
                        Text(
                            text = "发送",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    }
}
