package com.aitor.trackactividades.feed.data.response

import com.aitor.trackactividades.core.network.LocalDateTimeAdapter
import com.aitor.trackactividades.feed.presentation.model.Comment
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class CommentResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("userId") val userId: Int,
    @SerializedName("publicationId") val publicationId: Long,
    @SerializedName("comment") val comment: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("userImage") val userImage: String,
    @JsonAdapter(LocalDateTimeAdapter::class)
    @SerializedName("creationDate")
    val creationDate: LocalDateTime
) {
    fun toPresentation(): Comment {
        return Comment(
            id = id,
            userId = userId,
            publicationId = publicationId,
            comment = comment,
            userName = userName,
            userImage = userImage,
            creationDate = creationDate
        )
    }
}
