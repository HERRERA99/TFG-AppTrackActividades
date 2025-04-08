package com.aitor.trackactividades.feed.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aitor.trackactividades.core.token.TokenManager
import com.aitor.trackactividades.feed.data.response.PublicationResponse
import com.aitor.trackactividades.feed.presentation.model.Comment
import com.aitor.trackactividades.feed.presentation.model.Publication
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PublicationsRepository @Inject constructor(private val publicationsApiService: PublicationsApiService, private val tokenManager: TokenManager) {

    companion object {
        const val PAGE_SIZE = 4
        const val PREFETCH_ITEMS = 3
    }

    suspend fun createPublication(token: String, id: Long): Boolean {
        publicationsApiService.createPublication(token, id)
        return true
    }

    fun getCharacters(): Flow<PagingData<Publication>> {
        return Pager(config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PREFETCH_ITEMS),
            pagingSourceFactory = {
                PublicationPaginSource(
                    publicationsApiService,
                    tokenManager
                )
            }).flow
    }

    suspend fun addLike(token: String, id: Long, userId: Int): Publication {
        return publicationsApiService.addLike("Bearer $token", id, userId).toPresentation()
    }

    suspend fun removeLike(token: String, id: Long, userId: Int): Publication {
        return publicationsApiService.removeLike("Bearer $token", id, userId).toPresentation()
    }

    suspend fun getComments(token: String, id: Long): List<Comment> {
        return publicationsApiService.getComments("Bearer $token", id).map { it.toPresentation() }
    }

    suspend fun addComment(token: String, id: Long, userId: Int, text: String): Comment {
        return publicationsApiService.addComment("Bearer $token", id, userId, text).toPresentation()
    }

}