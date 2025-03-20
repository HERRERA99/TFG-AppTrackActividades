package com.aitor.trackactividades.authentication.presentation.model

import com.aitor.trackactividades.core.model.Gender
import java.time.LocalDate

data class RegisterModel (
    val username: String,
    val email: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    val birthdate: LocalDate,
    val weight: Double,
    val height: Int,
    val gender: Gender
)