package com.aitor.trackactividades.quedadas.presentation.Meetup

import com.aitor.trackactividades.core.model.Modalidades
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ItemMeetupList(
    val id: Long,
    val title: String,
    val dateTime: LocalDateTime,
    val location: String,
    val sportType: Modalidades,
    val isParticipating: Boolean
) {
    override fun toString(): String {
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        val formattedDate = dateTime.format(dateFormatter)
        val participationStatus = if (isParticipating) "Participando" else "No participando"

        return """
            ItemMeetupList:
            - ID: $id
            - Título: $title
            - Fecha y hora: $formattedDate
            - Ubicación: $location
            - Deporte: ${sportType.name}
            - Estado: $participationStatus
        """.trimIndent()
    }
}
