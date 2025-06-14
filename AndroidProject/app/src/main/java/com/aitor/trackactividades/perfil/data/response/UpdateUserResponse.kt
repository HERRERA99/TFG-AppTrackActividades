package com.aitor.trackactividades.perfil.data.response

import com.aitor.trackactividades.perfil.presentation.model.UpdateUser
import com.google.gson.annotations.SerializedName

data class UpdateUserResponse(
    @SerializedName("image") val image: String
) {
    fun toPresentation(): UpdateUser {
        return UpdateUser(
            image = image
        )
    }
}
