package com.aitor.trackactividades.feed.domain

import androidx.paging.PagingData
import com.aitor.trackactividades.feed.data.PublicationsRepository
import com.aitor.trackactividades.feed.presentation.model.Publication
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPublicPublicationsUseCase @Inject constructor(private val publicationsRepository: PublicationsRepository) {
    fun execute(): Flow<PagingData<Publication>> {
        return publicationsRepository.getPublications()
    }
}