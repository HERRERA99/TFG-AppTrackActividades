package com.aitor.trackactividades.quedadas.data

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface QuedadasApiService {

    @Multipart
    @POST("meetups")
    suspend fun createMeetup(
        @Header("Authorization") token: String,
        @Part("meetup") meetupJson: RequestBody,
        @Part gpxFile: MultipartBody.Part? = null
    ): Response<Void>
}