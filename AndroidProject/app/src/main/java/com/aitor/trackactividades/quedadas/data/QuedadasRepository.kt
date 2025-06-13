package com.aitor.trackactividades.quedadas.data

import android.content.Context
import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aitor.trackactividades.core.model.Modalidades
import com.aitor.trackactividades.core.network.LocalDateTimeQuedadasAdapter
import com.aitor.trackactividades.core.token.TokenManager
import com.aitor.trackactividades.quedadas.data.request.MeetupCreateDTO
import com.aitor.trackactividades.quedadas.data.request.MeetupResponse
import com.aitor.trackactividades.quedadas.presentation.model.ItemMeetupList
import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.LocalDateTime
import javax.inject.Inject

class QuedadasRepository @Inject constructor(
    private val quedadasApiService: QuedadasApiService,
    private val tokenManager: TokenManager
) {

    companion object {
        const val PAGE_SIZE = 10
        const val PREFETCH_ITEMS = 3
    }

    suspend fun createMeetup(
        title: String,
        description: String?,
        dateTime: LocalDateTime,
        location: String,
        locationCoordinates: LatLng,
        sportType: Modalidades,
        gpxUri: Uri?,
        context: Context
    ): Result<Boolean> {
        return try {
            val token = tokenManager.getToken()?.let { "Bearer $it" }
                ?: return Result.failure(Exception("Token no disponible"))

            val meetupDTO = MeetupCreateDTO(
                title = title,
                description = description,
                dateTime = dateTime,
                location = location,
                locationCoordinates = locationCoordinates,
                sportType = sportType,
            )

            val meetupJson = GsonBuilder()
                .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeQuedadasAdapter())
                .create()
                .toJson(meetupDTO)

            val meetupRequestBody = meetupJson.toRequestBody("application/json".toMediaType())

            val gpxPart = gpxUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                val fileName = uri.lastPathSegment ?: "route.gpx"
                val bytes = inputStream?.readBytes()
                inputStream?.close()
                bytes?.let {
                    MultipartBody.Part.createFormData(
                        "gpxFile",
                        fileName,
                        it.toRequestBody("application/octet-stream".toMediaTypeOrNull())
                    )
                }
            }

            val response = quedadasApiService.createMeetup(token, meetupRequestBody, gpxPart)

            if (response.isSuccessful) {
                Result.success(true)
            } else {
                val errorBody = response.errorBody()?.string()
                val errorMessage = if (errorBody?.contains("Error al procesar el archivo GPX") == true) {
                    "El archivo GPX no es válido"
                } else {
                    "Error al crear la quedada: ${response.code()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllMeetups(lat: Double, lng: Double): Flow<PagingData<ItemMeetupList>> {
        val token = tokenManager.getToken() ?: ""
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, prefetchDistance = PREFETCH_ITEMS),
            pagingSourceFactory = {
                QuedadasPaginSource(
                    quedadasApiService,
                    lat = lat,
                    lng = lng,
                    token = "Bearer $token",
                    pageSize = PAGE_SIZE
                )
            }
        ).flow
    }

    suspend fun getMeetup(id: Long): MeetupResponse {
        val token = tokenManager.getToken() ?: ""
        return quedadasApiService.getMeetup("Bearer $token", id)
    }

    suspend fun joinMeetup(id: Long): MeetupResponse {
        val token = tokenManager.getToken() ?: ""
        return quedadasApiService.joinMeetup("Bearer $token", id)
    }

    suspend fun leaveMeetup(id: Long): MeetupResponse {
        val token = tokenManager.getToken() ?: ""
        return quedadasApiService.leaveMeetup("Bearer $token", id)
    }

    suspend fun deleteMeetup(id: Long) {
        val token = tokenManager.getToken() ?: ""
        quedadasApiService.deleteMeetup("Bearer $token", id)
    }
}