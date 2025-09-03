package com.aitor.trackactividades.feed.presentation.model

import java.time.LocalDateTime

data class Comment(
    val id: Long,
    val userId: Int,
    val publicationId: Long,
    val comment: String,
    val userName: String,
    val userImage: String,
    val creationDate: LocalDateTime
)
