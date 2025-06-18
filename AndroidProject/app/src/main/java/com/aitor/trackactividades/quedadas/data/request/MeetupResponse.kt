package com.aitor.trackactividades.quedadas.data.request

import com.aitor.trackactividades.buscarUsuario.data.response.UserSearchResponse
import com.aitor.trackactividades.core.model.Modalidades
import com.aitor.trackactividades.quedadas.presentation.model.Meetup
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime
import java.time.OffsetDateTime

data class MeetupResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("dateTime") val date: String,
    @SerializedName("location") val location: String,
    @SerializedName("distance") val distance: Double,
    @SerializedName("elevationGain") val elevationGain: Double,
    @SerializedName("locationCoordinates") val locationCoordinates: LatLng,
    @SerializedName("sportType") val sportType: Modalidades,
    @SerializedName("organizerId") val organizerId: Int,
    @SerializedName("participants") val participants: List<UserSearchResponse>,
    @SerializedName("route") val route: List<LatLng>,
    @SerializedName("participating") val isParticipating: Boolean
) {
    fun toPresentation(): Meetup {
        return Meetup(
            id = this.id,
            title = this.title,
            description = this.description,
            dateTime = OffsetDateTime.parse(this.date),
            location = this.location,
            distance = this.distance,
            elevationGain = this.elevationGain,
            locationCoordinates = this.locationCoordinates,
            sportType = this.sportType,
            organizerId = this.organizerId,
            participants = this.participants.map { it.toPresentation() },
            route = this.route,
            isParticipating = this.isParticipating
        )
    }
}
