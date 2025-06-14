package com.aitor.trackactividades.perfil.data

import com.aitor.trackactividades.perfil.data.response.FollowResponse
import com.aitor.trackactividades.perfil.data.response.UnfollowResponse
import com.aitor.trackactividades.perfil.data.response.UpdateUserResponse
import com.aitor.trackactividades.perfil.data.response.UserProfileResponse
import com.aitor.trackactividades.perfil.data.response.UserResponse
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface UserApiService {
    @GET("/user/me")
    suspend fun getMyUser(
        @Header("Authorization") token: String
    ): UserResponse

    @GET("/user/{idUser}")
    suspend fun getUserById(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: Int
    ): UserProfileResponse

    @POST("/user/{followedId}/follow")
    suspend fun followUser(
        @Header("Authorization") token: String,
        @Path("followedId") followedId: Int
    ): FollowResponse

    @DELETE("/user/{followedId}/unfollow")
    suspend fun unfollowUser(
        @Header("Authorization") token: String,
        @Path("followedId") followedId: Int
    ): UnfollowResponse

    @Multipart
    @PATCH("/user/{idUser}/profile-picture")
    suspend fun updateProfilePicture(
        @Header("Authorization") token: String,
        @Path("idUser") idUser: Int,
        @Part image: MultipartBody.Part
    ): UpdateUserResponse
}