package com.aitor.trackactividades.authentication.presentation.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class RegisterModel (
    val username: String,
    val email: String,
    val password: String,
    val firstname: String,
    val lastname: String,
    val birthdate: LocalDate,
    val gender: Gender
)