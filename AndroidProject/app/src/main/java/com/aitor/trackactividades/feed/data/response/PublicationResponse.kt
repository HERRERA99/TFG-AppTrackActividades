package com.aitor.trackactividades.feed.data.response


import com.aitor.trackactividades.core.model.Modalidades
import com.aitor.trackactividades.core.network.LocalDateTimeAdapter
import com.aitor.trackactividades.feed.presentation.model.Publication
import com.aitor.trackactividades.feed.presentation.model.UserPublication
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class PublicationResponse(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("user") val user: UserResponse? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("startTime")
    @JsonAdapter(LocalDateTimeAdapter::class)
    val startTime: LocalDateTime? = null,
    @SerializedName("endTime")
    @JsonAdapter(LocalDateTimeAdapter::class)
    val endTime: LocalDateTime? = null,
    @SerializedName("activityType") val activityType: Modalidades? = null,
    @SerializedName("distance") val distance: Float = 0f,
    @SerializedName("duration") val duration: Long = 0L,
    @SerializedName("positiveElevation") val positiveElevation: Double = 0.0,
    @SerializedName("averageSpeed") val averageSpeed: Float = 0f,
    @SerializedName("calories") val calories: Float = 0f,
    @SerializedName("maxSpeed") val maxSpeed: Float = 0f,
    @SerializedName("speeds") val speeds: List<Float> = emptyList(),
    @SerializedName("elevations") val elevations: List<Double> = emptyList(),
    @SerializedName("route") val route: List<LatLng> = emptyList(),
    @SerializedName("maxAltitude") val maxAltitude: Double = 0.0,
    @SerializedName("likes") val likes: List<Int> = emptyList()
) {
    fun toPresentation(): Publication {
        return Publication(
            id = id,
            user = user?.toPresentation(),
            title = title,
            startTime = startTime,
            endTime = endTime,
            activityType = activityType,
            distance = distance,
            duration = duration,
            positiveElevation = positiveElevation,
            averageSpeed = averageSpeed,
            calories = calories,
            maxSpeed = maxSpeed,
            speeds = speeds,
            elevations = elevations,
            route = route,
            maxAltitude = maxAltitude,
            likes = likes
        )
    }
}
