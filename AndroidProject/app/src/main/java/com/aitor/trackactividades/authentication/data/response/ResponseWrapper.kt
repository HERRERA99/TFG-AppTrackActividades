package com.aitor.trackactividades.authentication.data.response

import com.aitor.trackactividades.authentication.presentation.model.TokenModel
import com.google.gson.annotations.SerializedName

data class ResponseWrapper(
    @SerializedName("token") val token: String
) {
    fun toPresentation(): TokenModel {
        return TokenModel(token)
    }
}
