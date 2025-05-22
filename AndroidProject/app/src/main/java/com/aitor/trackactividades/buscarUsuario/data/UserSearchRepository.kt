package com.aitor.trackactividades.buscarUsuario.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aitor.trackactividades.buscarUsuario.data.response.UserSearchResponse
import com.aitor.trackactividades.buscarUsuario.presentation.model.UserSearchModel
import com.aitor.trackactividades.core.token.TokenManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UserSearchRepository @Inject constructor(
    private val searchUserApiService: SearchUserApiService,
    private val tokenManager: TokenManager
) {

    companion object {
        const val PAGE_SIZE = 4
        const val PREFETCH_ITEMS = 3
    }

    suspend fun getUserSearch(text: String): Flow<PagingData<UserSearchModel>> {
        val token = tokenManager.getToken() ?: ""
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PREFETCH_ITEMS),
            pagingSourceFactory = {
                UserSearchPaginSource(
                    searchUserApiService,
                    text = text,
                    token = "Bearer $token",
                    pageSize = PAGE_SIZE
                )
            }
        ).flow
    }
}
