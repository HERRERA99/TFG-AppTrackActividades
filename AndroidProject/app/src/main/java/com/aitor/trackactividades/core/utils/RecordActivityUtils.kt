package com.aitor.trackactividades.core.utils

import com.aitor.trackactividades.core.model.Modalidades
import java.time.LocalDateTime

fun nombreAutomatico(horaInicio: LocalDateTime, tipoActividad: Modalidades): String {
    val hora = horaInicio.hour
    val parteDelDia = when (hora) {
        in 6..11 -> "por la mañana"
        in 12..17 -> "por la tarde"
        in 18..21 -> "por la noche"
        in 22..23 -> "por la noche"
        in 0..5 -> "al amanecer"
        else -> ""
    }
    return "${tipoActividad.displayName} $parteDelDia"
}

fun calcularDesnivelPositivo(altitudes: List<Double>): Double {
    var desnivelPositivo = 0.0

    for (i in 1 until altitudes.size) {
        val diferencia = altitudes[i] - altitudes[i - 1]
        if (diferencia > 0) {
            desnivelPositivo += diferencia
        }
    }

    return desnivelPositivo
}