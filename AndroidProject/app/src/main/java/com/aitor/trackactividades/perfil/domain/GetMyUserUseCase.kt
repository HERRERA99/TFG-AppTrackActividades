package com.aitor.trackactividades.perfil.domain

import com.aitor.trackactividades.authentication.data.AuthenticationRepository
import com.aitor.trackactividades.perfil.data.UserRepository
import com.aitor.trackactividades.perfil.data.response.UserResponse
import javax.inject.Inject

class GetMyUserUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(token: String): UserResponse {
        return userRepository.getMyUser(token)
    }
}