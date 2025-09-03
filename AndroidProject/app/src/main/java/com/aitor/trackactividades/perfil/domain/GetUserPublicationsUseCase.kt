package com.aitor.trackactividades.perfil.domain

import androidx.paging.PagingData
import com.aitor.trackactividades.feed.data.PublicationsRepository
import com.aitor.trackactividades.feed.presentation.model.Publication
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserPublicationsUseCase @Inject constructor(private val publicationsRepository: PublicationsRepository) {
    fun execute(userId: Int): Flow<PagingData<Publication>> {
        return publicationsRepository.getUserPublications(userId)
    }
}