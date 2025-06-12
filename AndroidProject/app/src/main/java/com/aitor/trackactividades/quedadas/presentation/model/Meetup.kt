package com.aitor.trackactividades.quedadas.presentation.model

import com.aitor.trackactividades.buscarUsuario.presentation.model.UserSearchModel
import com.aitor.trackactividades.core.model.Modalidades
import com.aitor.trackactividades.feed.data.response.UserResponse
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

data class Meetup(
    val id: Long,
    val title: String,
    val description: String?,
    val dateTime: LocalDateTime,
    val location: String,
    val distance: Double?,
    val elevationGain: Double?,
    val locationCoordinates: LatLng,
    val sportType: Modalidades,
    val organizerId: Int,
    val participants: List<UserSearchModel>,
    val route: List<LatLng>,
    val isParticipating: Boolean
)
