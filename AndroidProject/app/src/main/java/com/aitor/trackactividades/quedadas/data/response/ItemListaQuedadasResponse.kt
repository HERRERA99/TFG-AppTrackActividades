package com.aitor.trackactividades.quedadas.data.response

import com.aitor.trackactividades.core.model.Modalidades
import com.aitor.trackactividades.quedadas.presentation.Meetup.ItemMeetupList
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class ItemListaQuedadasResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("dateTime") val dateTime: String,
    @SerializedName("location") val location: String,
    @SerializedName("sportType") val sportType: Modalidades,
    @SerializedName("participating") val isParticipating: Boolean
) {
    fun toPresentation(): ItemMeetupList {
        return ItemMeetupList(
            id = this.id,
            title = this.title,
            dateTime = LocalDateTime.parse(this.dateTime),
            location = this.location,
            sportType = this.sportType,
            isParticipating = this.isParticipating
        )
    }
}
