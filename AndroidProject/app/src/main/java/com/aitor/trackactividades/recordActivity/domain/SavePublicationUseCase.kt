package com.aitor.trackactividades.recordActivity.domain

import com.aitor.trackactividades.feed.data.PublicationsRepository
import javax.inject.Inject

class SavePublicationUseCase @Inject constructor(private val publicationsRepository: PublicationsRepository) {
    suspend operator fun invoke(token: String, id: Long) {
        publicationsRepository.createPublication(token, id)
    }
}