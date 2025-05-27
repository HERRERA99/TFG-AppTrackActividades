package com.aitor.trackactividades.perfil.domain

import com.aitor.trackactividades.perfil.data.UserRepository
import com.aitor.trackactividades.perfil.presentation.model.FollowModel
import javax.inject.Inject

class AddFollowUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(followedId: Int): FollowModel {
        return userRepository.followUser(followedId).toFollowModel()
    }
}