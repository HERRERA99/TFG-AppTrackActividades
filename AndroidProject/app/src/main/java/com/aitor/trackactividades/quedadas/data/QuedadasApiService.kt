package com.aitor.trackactividades.quedadas.data

import com.aitor.trackactividades.quedadas.data.request.MeetupResponse
import com.aitor.trackactividades.quedadas.data.response.ListaQuedadasPageResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface QuedadasApiService {

    @Multipart
    @POST("/meetups")
    suspend fun createMeetup(
        @Header("Authorization") token: String,
        @Part("meetup") meetupJson: RequestBody,
        @Part gpxFile: MultipartBody.Part? = null
    ): Response<Void>

    @GET("/meetups/all")
    suspend fun getAllMeetups(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): ListaQuedadasPageResponse

    @GET("/meetups/{id}")
    suspend fun getMeetup(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): MeetupResponse
}