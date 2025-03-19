package com.aitor.trackactividades.authentication.data.request

import com.aitor.trackactividades.authentication.presentation.model.Gender
import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class RegisterRequest (
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("firstname") val firstname: String,
    @SerializedName("lastname") val lastname: String,
    @SerializedName("weight") val weight: Double,
    @SerializedName("height") val height: Int,
    @SerializedName("birthdate") val birthdate: String,
    @SerializedName("gender") val gender: String
)