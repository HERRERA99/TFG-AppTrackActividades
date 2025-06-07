package com.aitor.trackactividades.quedadas.presentation.Meetup

import com.aitor.trackactividades.core.model.Modalidades
import com.aitor.trackactividades.feed.data.response.UserResponse
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

data class Meetup(
    val id: Long,
    val title: String,
    val description: String,
    val dateTime: LocalDateTime,
    val location: String,
    val distance: Double?,
    val elevationGain: Double?,
    val maxParticipants: Int?,
    val locationCoordinates: LatLng,
    val sportType: Modalidades,
    val organizerId: UserResponse,
    val participants: List<UserResponse>,
    val route: List<LatLng>
)
