package com.aitor.trackactividades.authentication.data.response

import com.aitor.trackactividades.authentication.presentation.model.ValidateTokenModel
import com.google.gson.annotations.SerializedName

data class ValidResponse(
    @SerializedName("isValid") val isValid: Boolean,
    @SerializedName("message") val message: String
) {
    fun toPresentation(): ValidateTokenModel {
        return ValidateTokenModel(
            isValidate = isValid,
            message = message
        )
    }
}
