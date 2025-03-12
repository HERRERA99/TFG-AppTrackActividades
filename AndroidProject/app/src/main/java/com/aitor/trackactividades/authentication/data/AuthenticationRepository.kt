package com.aitor.trackactividades.authentication.data

import com.aitor.trackactividades.authentication.data.request.LoginRequest
import com.aitor.trackactividades.authentication.data.request.RegisterRequest
import com.aitor.trackactividades.authentication.presentation.model.TokenModel
import javax.inject.Inject

class AuthenticationRepository @Inject constructor(private val authenticationApiService: AuthenticationApiService) {

    suspend fun login(loginRequest: LoginRequest) : TokenModel {
        return authenticationApiService.login(loginRequest).toPresentation()
    }

    suspend fun register(registerRequest: RegisterRequest) : TokenModel {
        return authenticationApiService.register(registerRequest).toPresentation()
    }
}