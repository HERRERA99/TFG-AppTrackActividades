package com.aitor.trackactividades.authentication.data.request

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class RegisterRequest (
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("firstname") val firstname: String,
    @SerializedName("lastname") val lastname: String,
    @SerializedName("birthdate") val birthdate: LocalDate,
    @SerializedName("gender") val gender: String
)