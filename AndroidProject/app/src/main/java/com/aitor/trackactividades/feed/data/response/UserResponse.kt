package com.aitor.trackactividades.feed.data.response

import com.aitor.trackactividades.feed.presentation.model.UserPublication
import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("username") val username: String? = null,
    @SerializedName("imageUrl") val imageUrl: String? = null
) {
    fun toPresentation(): UserPublication {
        return UserPublication(
            username = username,
            imageUrl = imageUrl
        )
    }
}