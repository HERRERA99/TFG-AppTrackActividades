package com.aitor.trackactividades.buscarUsuario.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aitor.trackactividades.buscarUsuario.data.response.UserSearchResponse
import com.aitor.trackactividades.buscarUsuario.presentation.model.UserSearchModel
import com.aitor.trackactividades.feed.presentation.model.Publication
import javax.inject.Inject

class UserSearchPaginSource @Inject constructor(
    private val searchUserApiService: SearchUserApiService,
    private val text: String,
    private val token: String,
    private val pageSize: Int
) : PagingSource<Int, UserSearchModel>() {

    override fun getRefreshKey(state: PagingState<Int, UserSearchModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserSearchModel> {
        return try {
            val page = params.key ?: 0

            val response = searchUserApiService.searchUsers(token, text, page, pageSize)

            LoadResult.Page(
                data = response.content.map { it.toPresentation() },
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (page + 1 >= response.information.pages) null else page + 1
            )


        } catch (e: Exception) {
            println("Error loading users: ${e.message}")
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}
