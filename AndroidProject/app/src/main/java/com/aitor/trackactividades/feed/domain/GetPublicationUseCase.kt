package com.aitor.trackactividades.feed.domain

import com.aitor.trackactividades.feed.data.PublicationsRepository
import com.aitor.trackactividades.feed.presentation.model.Publication
import javax.inject.Inject

class GetPublicationUseCase @Inject constructor(private val publicationsRepository: PublicationsRepository) {
    suspend operator fun invoke(token: String, publicationId: Long): Publication {
        return publicationsRepository.getPublication(token, publicationId)
    }
}