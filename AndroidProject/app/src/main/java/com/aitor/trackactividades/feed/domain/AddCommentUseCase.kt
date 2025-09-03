package com.aitor.trackactividades.feed.domain

import com.aitor.trackactividades.feed.data.PublicationsRepository
import com.aitor.trackactividades.feed.presentation.model.Comment
import javax.inject.Inject

class AddCommentUseCase @Inject constructor(private val publicationsRepository: PublicationsRepository) {
    suspend operator fun invoke(token:String, id: Long, userId: Int, text: String): Comment {
        return publicationsRepository.addComment(token, id, userId, text)
    }

}