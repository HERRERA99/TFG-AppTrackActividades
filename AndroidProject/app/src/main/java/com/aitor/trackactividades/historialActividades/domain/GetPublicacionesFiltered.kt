package com.aitor.trackactividades.historialActividades.domain

import androidx.paging.PagingData
import com.aitor.trackactividades.feed.data.PublicationsRepository
import com.aitor.trackactividades.feed.presentation.model.Publication
import com.aitor.trackactividades.historialActividades.presentation.model.FiltroModel
import kotlinx.coroutines.flow.Flow

import javax.inject.Inject

class GetPublicacionesFiltered @Inject constructor(private val publicationsRepository: PublicationsRepository) {
    fun execute(filtro: FiltroModel): Flow<PagingData<Publication>> {
        return publicationsRepository.getPublicationsFiltered(filtro.toRequest())
    }
}