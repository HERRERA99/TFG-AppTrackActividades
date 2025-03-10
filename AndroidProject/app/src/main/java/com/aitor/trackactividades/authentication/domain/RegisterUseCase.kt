package com.aitor.trackactividades.authentication.domain

import com.aitor.trackactividades.authentication.data.AuthenticationRepository
import com.aitor.trackactividades.authentication.data.request.RegisterRequest
import com.aitor.trackactividades.authentication.presentation.model.RegisterModel
import com.aitor.trackactividades.authentication.presentation.model.TokenModel
import javax.inject.Inject

class RegisterUseCase @Inject constructor(private val authenticationRepository: AuthenticationRepository) {
    suspend operator fun invoke(registerModel: RegisterModel): TokenModel {
        return authenticationRepository.register(RegisterRequest(
            registerModel.username,
            registerModel.email,
            registerModel.password,
            registerModel.firstname,
            registerModel.lastname,
            registerModel.birthdate,
            registerModel.gender
        ))
    }
}