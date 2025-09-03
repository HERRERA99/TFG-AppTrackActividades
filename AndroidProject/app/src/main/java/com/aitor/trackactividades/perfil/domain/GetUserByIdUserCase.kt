package com.aitor.trackactividades.perfil.domain

import com.aitor.trackactividades.perfil.data.UserRepository
import javax.inject.Inject

class GetUserByIdUserCase @Inject constructor(private val userRepository: UserRepository) {
    suspend operator fun invoke(token: String, idUser: Int) = userRepository.getUserById(token, idUser).toPresentation()
}