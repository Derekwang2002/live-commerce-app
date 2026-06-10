package com.puskal.domain.comment

import com.puskal.data.model.CommentList
import com.puskal.data.repository.comment.CommentRepository
import javax.inject.Inject

class AddCommentUseCase @Inject constructor(private val commentRepository: CommentRepository) {
    operator fun invoke(videoId: String, text: String): CommentList {
        return commentRepository.addComment(videoId, text)
    }
}
