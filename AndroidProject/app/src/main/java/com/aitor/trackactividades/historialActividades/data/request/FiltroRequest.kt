package com.aitor.trackactividades.historialActividades.data.request

import com.aitor.trackactividades.core.model.Modalidades

data class FiltroRequest(
    var nombre: String? = null,
    var activityType: Modalidades? = null,

    var distanciaMin: Float = 0f,
    var distanciaMax: Float = 0f,

    var positiveElevationMin: Double = 0.0,
    var positiveElevationMax: Double = 0.0,

    var durationMin: Long = 0L,
    var durationMax: Long = 0L,

    var averageSpeedMin: Float = 0f,
    var averageSpeedMax: Float = 0f
)
