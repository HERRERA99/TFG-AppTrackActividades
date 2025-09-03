package com.aitor.trackactividades.recordActivity.presentation.utils

import com.aitor.trackactividades.core.model.Modalidades

object CaloriesManager {
    fun calculateCalories(weight: Double, speed: Float, activityType: Modalidades, timeInSeconds: Int): Float {
        val met = when (activityType) {
            Modalidades.CICLISMO_CARRETERA, Modalidades.CICLISMO_MONTAÑA -> when {
                speed >= 30 -> 14.0f
                speed >= 25 -> 12.0f
                speed >= 22 -> 10.0f
                speed >= 17 -> 8.0f
                speed >= 1 -> 4.0f
                else -> 0.0f
            }
            Modalidades.CORRER, Modalidades.CAMINATA -> when {
                speed >= 19 -> 20.0f
                speed >= 16 -> 15.0f
                speed >= 12 -> 11.8f
                speed >= 10 -> 9.8f
                speed >= 8 -> 8.3f
                speed >= 6 -> 6.0f
                speed >= 0.7 -> 2.0f
                else -> 0.0f
            }
        }
        return ((met * weight.toFloat() * (timeInSeconds.toFloat()) * 3.5f) / (200.0f * 60))
    }
}