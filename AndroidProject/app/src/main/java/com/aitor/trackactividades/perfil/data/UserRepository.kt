package com.aitor.trackactividades.perfil.data

import com.aitor.trackactividades.perfil.data.response.UserProfileResponse
import com.aitor.trackactividades.perfil.data.response.UserResponse
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userApiService: UserApiService
) {
    suspend fun getMyUser(token: String): UserResponse {
        return userApiService.getMyUser(token)
    }

    suspend fun getUserById(token: String, idUser: Int): UserProfileResponse {
        return userApiService.getUserById("Bearer $token", idUser)
    }
}