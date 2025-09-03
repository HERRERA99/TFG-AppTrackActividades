package com.aitor.trackactividades.authentication.domain

import com.aitor.trackactividades.authentication.presentation.model.LoginModel
import com.aitor.trackactividades.authentication.presentation.model.TokenModel
import com.aitor.trackactividades.perfil.data.UserRepository
import javax.inject.Inject

class ActualizarFcmTokenUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(token: String) {
        val body = mapOf("fcmToken" to token)
        userRepository.updateFcmToken(body)
    }
}