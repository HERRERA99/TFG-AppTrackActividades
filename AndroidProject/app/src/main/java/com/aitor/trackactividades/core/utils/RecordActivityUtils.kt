package com.aitor.trackactividades.core.utils

import com.aitor.trackactividades.core.model.Modalidades
import java.time.LocalDateTime

/**
 * Genera un nombre automático para una actividad basado en la hora de inicio y el tipo de actividad.
 *
 * Divide el día en franjas horarias y devuelve una descripción como "Ciclismo por la tarde".
 *
 * Franja horaria:
 * - 06:00 - 11:59 -> "por la mañana"
 * - 12:00 - 17:59 -> "por la tarde"
 * - 18:00 - 23:59 -> "por la noche"
 * - 00:00 - 05:59 -> "al amanecer"
 *
 * @param horaInicio Hora de inicio de la actividad.
 * @param tipoActividad Tipo de actividad, con un campo `displayName` para mostrar.
 * @return Cadena con el nombre automático, combinando tipo y momento del día.
 */
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

/**
 * Calcula el desnivel positivo total a partir de una lista de altitudes.
 *
 * El desnivel positivo es la suma de todos los aumentos de altitud a lo largo de una ruta.
 *
 * @param altitudes Lista de altitudes (en metros).
 * @return La suma total de ascensos positivos (en metros).
 *
 * Ejemplo:
 * ```
 * calcularDesnivelPositivo(listOf(100.0, 150.0, 130.0, 180.0)) // Devuelve 100.0
 * ```
 */
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