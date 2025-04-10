package com.aitor.trackactividades.recordActivity.data.request

import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class ActivityRequest(
    @SerializedName("id") val id: Long,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("activityType")val activityType: String,
    @SerializedName("endTime") val endTime: String,
    @SerializedName("distance") val distance: Float,
    @SerializedName("duration") val duration: Long,
    @SerializedName("positiveElevation") val positiveElevation: Double,
    @SerializedName("averageSpeed")val averageSpeed: Float,
    @SerializedName("calories") val calories: Float,
    @SerializedName("maxSpeed") val maxSpeed: Float,
    @SerializedName("speeds") val speeds: List<Float>,
    @SerializedName("elevations") val elevations: List<Double>,
    @SerializedName("maxAltitude") val maxAltitude: Double,
    @SerializedName("route") val route: List<LatLngRequest>,
    @SerializedName("distances") val distances: List<Float>,
    @SerializedName("title") val title: String,
    @SerializedName("isPublic") val isPublic: Boolean
)

data class LatLngRequest(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)