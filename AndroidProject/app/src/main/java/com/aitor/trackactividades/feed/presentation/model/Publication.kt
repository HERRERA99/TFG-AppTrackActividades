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
    val maxAltitude: Double,
    val likes: List<Int>
)
