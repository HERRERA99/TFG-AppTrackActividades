package com.aitor.trackactividades.feed.data

import com.aitor.trackactividades.core.model.Modalidades
import com.aitor.trackactividades.feed.data.response.CommentResponse
import com.aitor.trackactividades.feed.data.response.PageResponse
import com.aitor.trackactividades.feed.data.response.PublicationResponse
import com.aitor.trackactividades.historialActividades.data.request.FiltroRequest
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PublicationsApiService {
    @POST("/publications/{activityId}")
    suspend fun createPublication(
        @Header("Authorization") token: String,
        @Path("activityId") activityId: Long
    ): PublicationResponse

    @GET("/publications/public")
    suspend fun getPublicPublications(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int = 10
    ): PageResponse

    @GET("/publications/user/{userId}")
    suspend fun getUserPublications(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int = 10
    ): PageResponse

    @PUT("/publications/{id}/like")
    suspend fun addLike(
        @Header("Authorization") token: String,
        @Path("id") publicationId: Long,
        @Query("userId") userId: Int
    ): PublicationResponse

    @DELETE("/publications/{id}/removeLike")
    suspend fun removeLike(
        @Header("Authorization") token: String,
        @Path("id") publicationId: Long,
        @Query("userId") userId: Int
    ): PublicationResponse

    @GET("/publications/{id}/comment")
    suspend fun getComments(
        @Header("Authorization") token: String,
        @Path("id") publicationId: Long
    ): List<CommentResponse>

    @POST("/publications/{id}/comment")
    suspend fun addComment(
        @Header("Authorization") token: String,
        @Path("id") publicationId: Long,
        @Query("userId") userId: Int,
        @Query("text") text: String
    ): CommentResponse

    @GET("/publications/{id}")
    suspend fun getPublication(
        @Header("Authorization") token: String,
        @Path("id") publicationId: Long
    ) : PublicationResponse

    @GET("/publications/user/{id}/filtro")
    suspend fun getPublicationsByFilter(
        @Header("Authorization") token: String,
        @Path("id") userId: Int,
        @Query("page") page: Int,
        @Query("size") size: Int = 10,

        @Query("nombre") nombre: String? = null,
        @Query("activityType") activityType: Modalidades? = null,

        @Query("distanciaMin") distanciaMin: Float = 0f,
        @Query("distanciaMax") distanciaMax: Float = 0f,

        @Query("positiveElevationMin") positiveElevationMin: Double = 0.0,
        @Query("positiveElevationMax") positiveElevationMax: Double = 0.0,

        @Query("durationMin") durationMin: Long = 0L,
        @Query("durationMax") durationMax: Long = 0L,

        @Query("averageSpeedMin") averageSpeedMin: Float = 0f,
        @Query("averageSpeedMax") averageSpeedMax: Float = 0f
    ): PageResponse

}