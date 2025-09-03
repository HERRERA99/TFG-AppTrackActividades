package com.aitor.trackactividades.recordActivity.presentation.utils

import com.aitor.trackactividades.core.model.Modalidades
import java.util.Locale

object SpeedManager {
    fun speedConversor(speed: Float, modalidades: Modalidades): String {
        return when (modalidades) {
            Modalidades.CICLISMO_CARRETERA, Modalidades.CICLISMO_MONTAÑA -> {
                // Mostrar la velocidad en km/h sin conversión
                String.format(Locale.getDefault(), "%.2f km/h", speed)
            }
            Modalidades.CAMINATA, Modalidades.CORRER -> {
                // Convertir de km/h a min/km
                val minKm = if (speed > 0) 60f / speed else 0f
                String.format(Locale.getDefault(), "%.2f min/km", minKm)
            }
        }
    }
}