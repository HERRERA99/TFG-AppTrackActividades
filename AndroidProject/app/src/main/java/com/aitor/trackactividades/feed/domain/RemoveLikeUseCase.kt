package com.aitor.trackactividades.feed.domain

import com.aitor.trackactividades.feed.data.PublicationsRepository
import com.aitor.trackactividades.feed.presentation.model.Publication
import javax.inject.Inject

class RemoveLikeUseCase @Inject constructor(private val publicationsRepository: PublicationsRepository) {
    suspend operator fun invoke(token:String, id: Long, userId: Int): Publication {
        return publicationsRepository.removeLike(token, id, userId)
    }
}