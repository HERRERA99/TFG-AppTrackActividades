package com.aitor.trackactividades.perfil.data

import com.aitor.trackactividades.perfil.data.response.UserResponse
import retrofit2.http.GET
import retrofit2.http.Header
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
    ): UserResponse

}