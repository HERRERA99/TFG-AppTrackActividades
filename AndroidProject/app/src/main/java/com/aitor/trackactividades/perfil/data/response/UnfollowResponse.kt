package com.aitor.trackactividades.perfil.data.response

import com.aitor.trackactividades.perfil.presentation.model.UnfollowModel
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class UnfollowResponse(
    @SerializedName("unfollowedId") val unfollowedId: Int,
    @SerializedName("unfollowerId") val unfollowerId: Int
) {
    fun toUnfollowModel(): UnfollowModel {
        return UnfollowModel(
            unfollowedId = unfollowedId,
            unfollowerId = unfollowerId
        )
    }
}
