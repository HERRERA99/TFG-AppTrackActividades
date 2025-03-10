package com.aitor.trackactividades.authentication.domain

import com.aitor.trackactividades.authentication.data.AuthenticationRepository
import com.aitor.trackactividades.authentication.data.request.LoginRequest
import com.aitor.trackactividades.authentication.presentation.model.LoginModel
import com.aitor.trackactividades.authentication.presentation.model.TokenModel
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val authenticationRepository: AuthenticationRepository) {
    suspend operator fun invoke(loginModel: LoginModel): TokenModel {
        return authenticationRepository.login(LoginRequest(loginModel.identifier, loginModel.password))
    }
}