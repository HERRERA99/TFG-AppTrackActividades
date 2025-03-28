package com.aitor.trackactividades.authentication.domain

import com.aitor.trackactividades.authentication.data.AuthenticationRepository
import com.aitor.trackactividades.authentication.presentation.model.ValidateTokenModel
import javax.inject.Inject

class ValidateTokenUseCase @Inject constructor(private val authenticationRepository: AuthenticationRepository) {
    suspend operator fun invoke(token: String): ValidateTokenModel {
        return authenticationRepository.validateToken(token).toPresentation()
    }
}