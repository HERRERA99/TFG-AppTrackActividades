package com.aitor.trackactividades.quedadas.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.aitor.trackactividades.core.model.Modalidades
import com.aitor.trackactividades.core.network.LocalDateTimeAdapter
import com.aitor.trackactividades.core.network.LocalDateTimeQuedadasAdapter
import com.aitor.trackactividades.core.token.TokenManager
import com.aitor.trackactividades.core.userPreferences.UserPreferences
import com.aitor.trackactividades.quedadas.data.request.MeetupCreateDTO
import com.google.android.gms.maps.model.LatLng
import com.google.gson.GsonBuilder
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
    suspend fun createMeetup(
        title: String,
        description: String,
        dateTime: LocalDateTime,
        location: String,
        maxParticipants: Int?,
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
                maxParticipants = maxParticipants,
                locationCoordinates = locationCoordinates,
                sportType = sportType
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
}