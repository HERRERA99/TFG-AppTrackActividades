package com.aitor.trackactividades.core.utils

import java.time.Duration
import java.time.LocalDateTime

fun tiempoTranscurrido(fecha: LocalDateTime): String {
    val ahora = LocalDateTime.now()
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