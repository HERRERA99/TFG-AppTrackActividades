package com.aitor.trackactividades.buscarUsuario.domain

import androidx.paging.PagingData
import com.aitor.trackactividades.buscarUsuario.data.UserSearchRepository
import com.aitor.trackactividades.buscarUsuario.data.response.UserSearchResponse
import com.aitor.trackactividades.buscarUsuario.presentation.model.UserSearchModel
import com.aitor.trackactividades.feed.presentation.model.Publication
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserSearchList @Inject constructor(private val userSearchRepository: UserSearchRepository) {
    suspend fun execute(text: String): Flow<PagingData<UserSearchModel>> {
        return userSearchRepository.getUserSearch(text)
    }
}