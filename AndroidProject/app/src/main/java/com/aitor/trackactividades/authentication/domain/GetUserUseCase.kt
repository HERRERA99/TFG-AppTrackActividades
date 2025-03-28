package com.aitor.trackactividades.authentication.domain

import com.aitor.trackactividades.authentication.data.AuthenticationRepository
import com.aitor.trackactividades.authentication.data.response.UserResponse
import javax.inject.Inject

class GetUserUseCase @Inject constructor(private val authenticationRepository: AuthenticationRepository) {
    suspend operator fun invoke(token: String): UserResponse {
        return authenticationRepository.getUser(token)
    }
}