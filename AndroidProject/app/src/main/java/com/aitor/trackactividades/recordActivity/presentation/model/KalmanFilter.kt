package com.aitor.trackactividades.recordActivity.presentation.model

import android.location.Location
import kotlin.math.*

class KalmanFilter(
    initialLocation: Location,
    private val processNoise: Float = 1f,
    private val measurementNoise: Float = 10f
) {
    private var predictedLatitude: Double = initialLocation.latitude
    private var predictedLongitude: Double = initialLocation.longitude
    private var predictedAltitude: Double = initialLocation.altitude
    private var predictedSpeed: Double = initialLocation.speed.toDouble()
    private var predictedVariance: Float = 1f
    private var kalmanGain: Float = 0f
    private var lastUpdateTime: Long = System.currentTimeMillis()

    fun update(measuredLocation: Location): Location {
        val currentTime = System.currentTimeMillis()
        val deltaTime = (currentTime - lastUpdateTime) / 1000.0 // Convertir a segundos
        if (deltaTime <= 0) return measuredLocation // Evitar cálculos innecesarios
        lastUpdateTime = currentTime

        // 🔹 Cálculo de la conversión más precisa de metros a grados
        val latFactor = 111320.0
        val lonFactor = 111320.0 * cos(Math.toRadians(predictedLatitude))

        // 🔹 Cálculo del rumbo estimado entre la última posición filtrada y la nueva
        val deltaLat = measuredLocation.latitude - predictedLatitude
        val deltaLon = measuredLocation.longitude - predictedLongitude
        val estimatedBearing = atan2(deltaLon * lonFactor, deltaLat * latFactor).toDegrees()

        // 🔹 Predicción basada en la última velocidad y rumbo estimado
        val predictedDistance = predictedSpeed * deltaTime
        predictedLatitude += (predictedDistance * cos(Math.toRadians(estimatedBearing))) / latFactor
        predictedLongitude += (predictedDistance * sin(Math.toRadians(estimatedBearing))) / lonFactor

        predictedVariance += processNoise

        // 🔹 Cálculo de la ganancia de Kalman
        kalmanGain = predictedVariance / (predictedVariance + measurementNoise)

        // 🔹 Corrección de la predicción
        predictedLatitude += kalmanGain * (measuredLocation.latitude - predictedLatitude)
        predictedLongitude += kalmanGain * (measuredLocation.longitude - predictedLongitude)
        predictedAltitude += kalmanGain * (measuredLocation.altitude - predictedAltitude)
        predictedSpeed += kalmanGain * (measuredLocation.speed - predictedSpeed)

        predictedVariance *= (1 - kalmanGain)

        // 🔹 Crear una nueva instancia de Location con los valores filtrados
        return Location(measuredLocation.provider).apply {
            latitude = predictedLatitude
            longitude = predictedLongitude
            altitude = predictedAltitude
            speed = predictedSpeed.toFloat()
            bearing = estimatedBearing.toFloat()
        }
    }

    // Función de extensión para convertir radianes a grados
    private fun Double.toDegrees() = this * (180.0 / Math.PI)
}
