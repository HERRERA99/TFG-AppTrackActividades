package com.aitor.trackactividades.authentication.data.response

import com.aitor.trackactividades.core.model.Gender
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
data class UserResponse(
    val id: Int,
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
}

