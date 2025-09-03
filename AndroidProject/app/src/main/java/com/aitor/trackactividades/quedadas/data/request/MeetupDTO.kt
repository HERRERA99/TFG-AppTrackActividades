package com.aitor.trackactividades.quedadas.data.request

import com.aitor.trackactividades.core.model.Modalidades
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import java.time.OffsetDateTime

data class MeetupCreateDTO(
    val title: String,
    val description: String?,
    val dateTime: String,
    val location: String,
    val locationCoordinates: LatLng,
    val sportType: Modalidades,
)
