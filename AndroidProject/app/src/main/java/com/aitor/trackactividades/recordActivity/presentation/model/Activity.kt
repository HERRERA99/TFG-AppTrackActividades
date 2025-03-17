package com.aitor.trackactividades.recordActivity.presentation.model

import com.google.android.gms.maps.model.LatLng

data class Activity(
    val inicio: Long,
    val fin: Long,
    val elapsedTime: List<Long>,
    val distancia: List<Float>,
    val velocidad: List<Float>,
    val altitud: List<Float>,
    val pendiente: List<Float>,
    val ruta: List<LatLng>,
    val velocidadPromedio: Float,
    val desnivel: Float,
    val velocidadMaxima: Float = velocidad.maxOrNull() ?: 0f,
    val altitudMax: Float = altitud.maxOrNull() ?: 0f,
    val altitudMin: Float = altitud.minOrNull() ?: 0f,
    val pendienteMaxima: Float = pendiente.maxOrNull() ?: 0f,
    val duracion: Long = fin - inicio
)

