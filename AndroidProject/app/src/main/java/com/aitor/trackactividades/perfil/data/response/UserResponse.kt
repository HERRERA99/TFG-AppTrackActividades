package com.aitor.trackactividades.perfil.data.response

import com.aitor.trackactividades.core.model.Gender
import com.aitor.trackactividades.perfil.presentation.model.UserModel
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Int,
    val image: String?,
    val username: String,
    val nombre: String,
    val apellidos: String,
    val email: String,
    val peso: Double,
    val altura: Int,
    val genero: Gender
) {
    override fun toString(): String {
        return """
            UserResponse(
                id=$id,
                image=$image,
                username='$username',
                nombre='$nombre',
                apellidos='$apellidos',
                email='$email',
                peso=$peso,
                altura=$altura,
                genero='$genero'
            )
        """.trimIndent()
    }

    fun toPresentation(): UserModel {
        return UserModel(
            id = id,
            image = image,
            username = username,
            nombre = nombre,
            apellidos = apellidos,
            email = email,
            peso = peso,
            altura = altura,
            genero = genero
        )
    }
}

