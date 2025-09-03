package com.aitor.trackactividades.core.utils

/**
 * Formatea un valor de tiempo en segundos (con decimales) a una cadena legible con formato de horas, minutos y segundos.
 *
 * @param x El valor en segundos a formatear. Puede incluir decimales, pero solo se considera la parte entera.
 * @return Una cadena que representa el tiempo en el formato más adecuado:
 *         - "Xh Ymin Zs" si x >= 3600
 *         - "Xmin Ys" si x >= 60
 *         - "Xs" si x < 60
 *
 * Ejemplos:
 * - formatSeconds(3661.8) → "1h 1min 1s"
 * - formatSeconds(3600.0) → "1h"
 * - formatSeconds(125.0) → "2min 5s"
 * - formatSeconds(59.9) → "59s"
 */
fun formatSeconds(
    x: Double,
): String {
    val totalSeconds = x.toInt()

    return when {
        totalSeconds >= 3600 -> {
            val hours = totalSeconds / 3600
            val remainingMinutes = (totalSeconds % 3600) / 60
            val remainingSeconds = totalSeconds % 60

            if (remainingMinutes > 0 || remainingSeconds > 0) {
                "${hours}h ${remainingMinutes}min ${remainingSeconds}s"
            } else {
                "${hours}h"
            }
        }
        totalSeconds >= 60 -> {
            val minutes = totalSeconds / 60
            val remainingSeconds = totalSeconds % 60

            if (remainingSeconds > 0) {
                "${minutes}min ${remainingSeconds}s"
            } else {
                "${minutes}min"
            }
        }
        else -> "${totalSeconds}s"
    }
}