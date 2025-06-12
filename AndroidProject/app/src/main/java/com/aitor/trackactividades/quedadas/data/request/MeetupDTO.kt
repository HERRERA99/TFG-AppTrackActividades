package com.aitor.trackactividades.quedadas.data.request

import com.aitor.trackactividades.core.model.Modalidades
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

data class MeetupCreateDTO(
    val title: String,
    val description: String?,
    val dateTime: LocalDateTime,
    val location: String,
    val locationCoordinates: LatLng,
    val sportType: Modalidades,
)
