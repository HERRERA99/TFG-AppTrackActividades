package com.aitor.trackactividades.historialActividades.data


import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.aitor.trackactividades.core.token.TokenManager
import com.aitor.trackactividades.core.userPreferences.UserPreferences
import com.aitor.trackactividades.feed.data.PublicationsApiService
import com.aitor.trackactividades.feed.data.response.PublicationResponse
import com.aitor.trackactividades.feed.presentation.model.Publication
import com.aitor.trackactividades.historialActividades.data.request.FiltroRequest
import java.io.IOException
import javax.inject.Inject

class PublicationsFilterPaginSource @Inject constructor(
    private val publicationsApiService: PublicationsApiService,
    private val tokenManager: TokenManager,
    private val userPreferences: UserPreferences,
    private val filtro: FiltroRequest
) :

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

            val response = publicationsApiService.getPublicationsByFilter(
                token = token,
                userId = userPreferences.getId()!!,
                page = page,
                size = params.loadSize,

                nombre = filtro.nombre,
                activityType = filtro.activityType,

                distanciaMin = filtro.distanciaMin,
                distanciaMax = filtro.distanciaMax,

                positiveElevationMin = filtro.positiveElevationMin,
                positiveElevationMax = filtro.positiveElevationMax,

                durationMin = filtro.durationMin,
                durationMax = filtro.durationMax,

                averageSpeedMin = filtro.averageSpeedMin,
                averageSpeedMax = filtro.averageSpeedMax
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