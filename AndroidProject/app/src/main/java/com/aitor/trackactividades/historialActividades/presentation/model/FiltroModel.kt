package com.aitor.trackactividades.historialActividades.presentation.model

import com.aitor.trackactividades.core.model.Modalidades
import com.aitor.trackactividades.historialActividades.data.request.FiltroRequest

data class FiltroModel(
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
) {
    fun toRequest(): FiltroRequest {
        return FiltroRequest(
            nombre = nombre,
            activityType = activityType,
            distanciaMin = distanciaMin,
            distanciaMax = distanciaMax,
            positiveElevationMin = positiveElevationMin,
            positiveElevationMax = positiveElevationMax,
            durationMin = durationMin,
            durationMax = durationMax,
            averageSpeedMin = averageSpeedMin,
            averageSpeedMax = averageSpeedMax
        )
    }
}
