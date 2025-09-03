package com.aitor.trackactividades.feed.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aitor.trackactividades.core.token.TokenManager
import com.aitor.trackactividades.feed.data.response.PublicationResponse
import com.aitor.trackactividades.feed.presentation.model.Publication
import java.io.IOException
import javax.inject.Inject

class PublicationPaginSource @Inject constructor(private val publicationsApiService: PublicationsApiService, private val tokenManager: TokenManager) :

    PagingSource<Int, Publication>() {
    override fun getRefreshKey(state: PagingState<Int, Publication>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Publication> {
        return try {
            val page = params.key ?: 1
            val token = tokenManager.getToken()?.trim()?.let { "Bearer $it" } ?: ""

            val response = publicationsApiService.getPublicPublications(
                token,
                page,
                params.loadSize
            )

            LoadResult.Page(
                data = response.results.map { it.toPresentation() },
                prevKey = if (response.information.previous == null) null else page - 1,
                nextKey = if (response.information.next == null) null else page + 1
            )
        } catch (e: Exception) {
            println("Error loading publications: ${e.message}")
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}