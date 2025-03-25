package com.aitor.trackactividades.recordActivity.data.response

import Activity
import com.aitor.trackactividades.core.model.Modalidades
import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import java.time.LocalDateTime

data class ActivityResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("startTime") val startTime: String,
    @SerializedName("activityType") val activityType: String,
    @SerializedName("endTime") val endTime: String,
    @SerializedName("distance") val distance: Float,
    @SerializedName("duration") val duration: Long,
    @SerializedName("positiveElevation") val positiveElevation: Double,
    @SerializedName("averageSpeed") val averageSpeed: Float,
    @SerializedName("calories") val calories: Float,
    @SerializedName("maxSpeed") val maxSpeed: Float,
    @SerializedName("speeds") val speeds: List<Float>,
    @SerializedName("elevations") val elevations: List<Double>,
    @SerializedName("maxAltitude") val maxAltitude: Double,
    @SerializedName("route") val route: List<LatLngResponse>,
    @SerializedName("title") val title: String,
    @SerializedName("isPublic") val isPublic: Boolean,
    @SerializedName("userId") val userId: Long
) {
    fun toPresentation(): Activity {
        return Activity(
            id = id,
            horaInicio = LocalDateTime.parse(startTime),
            tipoActividad = Modalidades.valueOf(activityType),
            horaFin = LocalDateTime.parse(endTime),
            distancia = distance,
            duracion = duration,
            desnivelPositivo = positiveElevation,
            velocidadMedia = averageSpeed,
            calorias = calories,
            velocidadMaxima = maxSpeed,
            velocidades = speeds,
            desniveles = elevations,
            altitudMaxima = maxAltitude,
            ruta = route.map { LatLng(it.latitude, it.longitude) },
            titulo = title,
            isPublic = isPublic
        )
    }
}

data class LatLngResponse(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)