package com.aitor.trackactividades.buscarUsuario.data.response

import com.aitor.trackactividades.buscarUsuario.presentation.model.UserSearchModel
import com.google.gson.annotations.SerializedName

data class UserSearchResponse (
    @SerializedName("id") val id: Int,
    @SerializedName("image") val image: String?,
    @SerializedName("username") val userName: String?,
    @SerializedName("name") val name: String?
) {
    fun toPresentation(): UserSearchModel {
        return UserSearchModel(
            id = this.id,
            image = this.image ?: "",
            userName = this.userName ?: "Desconocido",
            name = this.name ?: ""
        )
    }
}
