package com.aitor.trackactividades.perfil.domain

import com.aitor.trackactividades.perfil.data.UserRepository
import com.aitor.trackactividades.perfil.presentation.model.UnfollowModel
import javax.inject.Inject

class RemoveFollowUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(followedId: Int): UnfollowModel {
        return userRepository.unfollowUser(followedId).toUnfollowModel()
    }
}
