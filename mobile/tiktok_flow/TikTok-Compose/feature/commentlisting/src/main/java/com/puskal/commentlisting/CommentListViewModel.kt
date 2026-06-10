package com.puskal.commentlisting

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.puskal.core.base.BaseViewModel
import com.puskal.core.DestinationRoute.PassedKey.VIDEO_ID
import com.puskal.data.source.VideoDataSource
import com.puskal.domain.comment.AddCommentUseCase
import com.puskal.domain.comment.GetCommentOnVideoUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Puskal Khadka on 3/22/2023.
 */
@HiltViewModel
class CommentListViewModel @Inject constructor(
    private val getCommentOnVideoUseCase: GetCommentOnVideoUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context
) : BaseViewModel<ViewState, CommentEvent>() {

    init {
        getComments()
    }

    private fun getComments() {
        val videoId = savedStateHandle.get<String>(VIDEO_ID).orEmpty()
        viewModelScope.launch {
            getCommentOnVideoUseCase(videoId).collect {
                updateState(
                    ViewState(
                        comments = it,
                        searchRecommendation = VideoDataSource.findVideoById(
                            context = context,
                            videoId = videoId
                        )?.searchRecommendation
                    )
                )
            }
        }
    }

    override fun onTriggerEvent(event: CommentEvent) {
        when (event) {
            is CommentEvent.SubmitComment -> submitComment(event.text)
        }
    }

    private fun submitComment(text: String) {
        val trimmedText = text.trim()
        if (trimmedText.isEmpty()) return

        val videoId = savedStateHandle.get<String>(VIDEO_ID).orEmpty()
        val comments = addCommentUseCase(videoId, trimmedText)
        updateState(
            viewState.value?.copy(comments = comments) ?: ViewState(comments = comments)
        )
    }

}
