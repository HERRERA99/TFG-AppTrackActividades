package com.aitor.trackactividades.authentication.data

import com.aitor.trackactividades.authentication.data.request.LoginRequest
import com.aitor.trackactividades.authentication.data.request.RegisterRequest
import com.aitor.trackactividades.authentication.data.response.ResponseWrapper
import com.aitor.trackactividades.authentication.data.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthenticationApiService {

    @POST("/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): ResponseWrapper

    @POST("/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): ResponseWrapper

    @GET("user/me")
    suspend fun getUser(
        @Header("Authorization") token: String,
        @Query("identifier") identifier: String
    ): UserResponse
}