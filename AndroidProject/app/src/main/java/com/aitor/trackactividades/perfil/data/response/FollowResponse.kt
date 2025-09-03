package com.aitor.trackactividades.perfil.data.response

import com.aitor.trackactividades.perfil.presentation.model.FollowModel
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class FollowResponse(
    @SerializedName("followedId") val followedId: Int,
    @SerializedName("followerId") val followerId: Int
) {
    fun toFollowModel(): FollowModel {
        return FollowModel(
            followedId = followedId,
            followerId = followerId
        )
    }
}
