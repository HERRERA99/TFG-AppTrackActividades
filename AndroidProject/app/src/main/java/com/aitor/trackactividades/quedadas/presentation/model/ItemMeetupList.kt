package com.aitor.trackactividades.quedadas.presentation.model

import com.aitor.trackactividades.core.model.Modalidades
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

data class ItemMeetupList(
    val id: Long,
    val title: String,
    val dateTime: LocalDateTime,
    val location: String,
    val sportType: Modalidades,
    val isParticipating: Boolean,
    val isOrganizer: Boolean
)
