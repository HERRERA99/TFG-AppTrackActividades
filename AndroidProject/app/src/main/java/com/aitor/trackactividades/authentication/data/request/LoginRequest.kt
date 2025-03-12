package com.aitor.trackactividades.authentication.data.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("identifier") val identifier: String,
    @SerializedName("password") val password: String
)
