package com.aitor.trackactividades.feed.domain

import com.aitor.trackactividades.feed.data.PublicationsRepository
import com.aitor.trackactividades.feed.presentation.model.Comment
import javax.inject.Inject

class GetCommentsUseCase @Inject constructor(private val publicationsRepository: PublicationsRepository) {
    suspend operator fun invoke(token:String, publicationId: Long): List<Comment> {
        return publicationsRepository.getComments(token, publicationId)
    }
}