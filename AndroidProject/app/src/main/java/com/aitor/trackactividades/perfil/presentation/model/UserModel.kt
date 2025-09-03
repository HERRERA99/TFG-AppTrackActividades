package com.aitor.trackactividades.perfil.presentation.model

import com.aitor.trackactividades.core.model.Gender

data class UserModel(
    val id: Int,
    val image: String?,
    val username: String,
    val nombre: String,
    val apellidos: String,
    val email: String,
    val peso: Double,
    val altura: Int,
    val genero: Gender,
    val followersCount: Int,
    val followingCount: Int,
    val isFollowing: Boolean
) {
    override fun toString(): String {
        return "UserModel(id=$id, image=$image, username='$username', nombre='$nombre', apellidos='$apellidos', email='$email', peso=$peso, altura=$altura, genero=$genero, followersCount=$followersCount, followingCount=$followingCount, isFollowing=$isFollowing)"
    }
}
