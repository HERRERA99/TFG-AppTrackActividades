package com.aitor.trackactividades.core.utils

import java.time.Duration
import java.time.LocalDateTime

/**
 * Calcula el tiempo transcurrido desde una fecha dada hasta el momento actual y lo devuelve
 * en una forma legible en español (ej. "hace 3 días", "ayer", "hace 2 horas").
 *
 * @param fecha La fecha y hora de referencia desde la cual calcular el tiempo transcurrido.
 * @return Una cadena que indica cuánto tiempo ha pasado desde la fecha especificada hasta ahora.
 *
 * Las salidas posibles son:
 * - "justo ahora" si han pasado menos de 1 minuto.
 * - "hace X minuto(s)" si han pasado menos de 60 minutos.
 * - "hace X hora(s)" si han pasado menos de 24 horas.
 * - "ayer" si han pasado exactamente 1 día.
 * - "hace X día(s)" si han pasado menos de 7 días.
 * - "hace X semana(s)" si han pasado menos de 30 días.
 * - "hace X mes(es)" si han pasado menos de 365 días.
 * - "hace X año(s)" si han pasado 365 días o más.
 */
object PublicationUtils {
    fun tiempoTranscurrido(
        fecha: LocalDateTime,
        ahora: LocalDateTime = LocalDateTime.now()
    ): String {
        val duracion = Duration.between(fecha, ahora)

        return when {
            duracion.toMinutes() < 1 -> "justo ahora"
            duracion.toMinutes() < 60 -> "hace ${duracion.toMinutes()} minuto(s)"
            duracion.toHours() < 24 -> "hace ${duracion.toHours()} hora(s)"
            duracion.toDays() == 1L -> "ayer"
            duracion.toDays() < 7 -> "hace ${duracion.toDays()} día(s)"
            duracion.toDays() < 30 -> "hace ${duracion.toDays() / 7} semana(s)"
            duracion.toDays() < 365 -> "hace ${duracion.toDays() / 30} mes(es)"
            else -> "hace ${duracion.toDays() / 365} año(s)"
        }
    }
}