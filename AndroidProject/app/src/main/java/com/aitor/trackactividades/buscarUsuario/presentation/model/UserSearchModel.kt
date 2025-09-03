package com.aitor.trackactividades.buscarUsuario.presentation.model

import com.google.gson.annotations.SerializedName

data class UserSearchModel(
    val id: Int,
    val image: String,
    val userName: String,
    val name: String
)
