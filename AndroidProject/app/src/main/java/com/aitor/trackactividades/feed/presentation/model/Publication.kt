package com.aitor.trackactividades.feed.presentation.model

import com.aitor.trackactividades.core.model.Modalidades
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

data class Publication(
    val id: Long?,
    val user: UserPublication?,
    val title: String?,
    val startTime: LocalDateTime?,
    val endTime: LocalDateTime?,
    val activityType: Modalidades?,
    val distance: Float,
    val duration: Long,
    val positiveElevation: Double,
    val averageSpeed: Float,
    val calories: Float,
    val maxSpeed: Float,
    val speeds: List<Float>,
    val elevations: List<Double>,
    val route: List<LatLng>,
    val distances: List<Float>,
    val maxAltitude: Double,
    val likes: List<Int>
) {
    /**
     * Tiempo en ms a formato hh:mm:ss
     */
    fun formatDuration(): String {
        val secondsTotal = duration / 1000
        val hours = secondsTotal / 3600
        val minutes = (secondsTotal % 3600) / 60
        val seconds = secondsTotal % 60

        return buildString {
            if (hours > 0) append("${hours}h ")
            if (minutes > 0 || hours > 0) append("${minutes}m ")
            append("${seconds}s")
        }.trim()
    }

    fun formatDistance(): String {
        return if (distance < 1000) {
            "${distance.toInt()} m"
        } else {
            "%.2f km".format(distance / 1000)
        }
    }
}
