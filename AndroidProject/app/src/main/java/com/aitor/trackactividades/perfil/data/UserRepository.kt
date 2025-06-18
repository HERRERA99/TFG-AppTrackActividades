package com.aitor.trackactividades.perfil.data

import com.aitor.trackactividades.core.token.TokenManager
import com.aitor.trackactividades.perfil.data.response.FollowResponse
import com.aitor.trackactividades.perfil.data.response.UnfollowResponse
import com.aitor.trackactividades.perfil.data.response.UpdateUserResponse
import com.aitor.trackactividades.perfil.data.response.UserProfileResponse
import com.aitor.trackactividades.perfil.data.response.UserResponse
import com.aitor.trackactividades.perfil.presentation.model.FollowModel
import com.aitor.trackactividades.perfil.presentation.model.UnfollowModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userApiService: UserApiService,
    private val tokenManager: TokenManager
) {
    suspend fun getMyUser(token: String): UserResponse {
        return userApiService.getMyUser(token)
    }

    suspend fun getUserById(token: String, idUser: Int): UserProfileResponse {
        return userApiService.getUserById("Bearer $token", idUser)
    }

    suspend fun followUser(followedId: Int): FollowResponse {
        return userApiService.followUser("Bearer ${tokenManager.getToken()}", followedId)
    }

    suspend fun unfollowUser(followedId: Int): UnfollowResponse {
        return userApiService.unfollowUser("Bearer ${tokenManager.getToken()}", followedId)
    }

    suspend fun updateProfilePicture(idUser: Int, imageFile: File): UpdateUserResponse {
        val token = tokenManager.getToken()

        val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val multipartBody = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

        return userApiService.updateProfilePicture("Bearer $token", idUser, multipartBody)
    }

    suspend fun updateFcmToken(tokenBody: Map<String, String>): Response<Unit> {
        val authToken = "Bearer ${tokenManager.getToken()}"
        return userApiService.updateFcmToken(authToken, tokenBody)
    }
}

