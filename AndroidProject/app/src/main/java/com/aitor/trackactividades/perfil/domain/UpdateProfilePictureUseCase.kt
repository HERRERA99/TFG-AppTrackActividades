package com.aitor.trackactividades.perfil.domain

import com.aitor.trackactividades.perfil.data.UserRepository
import com.aitor.trackactividades.perfil.presentation.model.UpdateUser
import java.io.File
import javax.inject.Inject

class UpdateProfilePictureUseCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(idUser: Int, imageFile: File): UpdateUser {
        return userRepository.updateProfilePicture(idUser, imageFile).toPresentation()
    }
}