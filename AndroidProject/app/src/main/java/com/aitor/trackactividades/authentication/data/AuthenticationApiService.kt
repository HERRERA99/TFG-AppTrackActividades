package com.aitor.trackactividades.authentication.data

import com.aitor.trackactividades.authentication.data.request.LoginRequest
import com.aitor.trackactividades.authentication.data.request.RegisterRequest
import com.aitor.trackactividades.authentication.data.response.ResponseWrapper
import com.aitor.trackactividades.authentication.data.response.ValidResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthenticationApiService {

    @POST("/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): ResponseWrapper

    @POST("/auth/register")
    suspend fun register(@Body registerRequest: RegisterRequest): ResponseWrapper

    @POST("/auth/validateToken")
    suspend fun validateToken(
        @Header("Authorization") token: String
    ): ValidResponse
}