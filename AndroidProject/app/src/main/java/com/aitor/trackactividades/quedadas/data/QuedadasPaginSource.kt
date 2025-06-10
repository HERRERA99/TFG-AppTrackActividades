package com.aitor.trackactividades.quedadas.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aitor.trackactividades.quedadas.presentation.model.ItemMeetupList
import javax.inject.Inject

class QuedadasPaginSource @Inject constructor(
    private val quedadasApiService: QuedadasApiService,
    private val lat: Double,
    private val lng: Double,
    private val token: String,
    private val pageSize: Int
) : PagingSource<Int, ItemMeetupList>() {

    override fun getRefreshKey(state: PagingState<Int, ItemMeetupList>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemMeetupList> {
        return try {
            val page = params.key ?: 1

            val response = quedadasApiService.getAllMeetups(token, page, pageSize, lat, lng)

            LoadResult.Page(
                data = response.content.map { it.toPresentation() },
                prevKey = if (response.information.previous == null) null else page - 1,
                nextKey = if (response.information.next == null) null else page + 1
            )


        } catch (e: Exception) {
            println("Error loading users: ${e.message}")
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}