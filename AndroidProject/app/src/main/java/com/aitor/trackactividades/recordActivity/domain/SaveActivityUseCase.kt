package com.aitor.trackactividades.recordActivity.domain

import com.aitor.trackactividades.recordActivity.data.ActivitiesRepository
import com.aitor.trackactividades.recordActivity.data.request.ActivityRequest
import com.aitor.trackactividades.recordActivity.data.request.LatLngRequest
import javax.inject.Inject

class SaveActivityUseCase @Inject constructor(private val activitiesRepository: ActivitiesRepository) {
    suspend operator fun invoke(token: String, activity: Activity) {
        activitiesRepository.createActivity(token, ActivityRequest(
            id = activity.id,
            startTime = activity.horaInicio.toString(),
            activityType = activity.tipoActividad.name,
            endTime = activity.horaFin.toString(),
            distance = activity.distancia,
            duration = activity.duracion,
            positiveElevation = activity.desnivelPositivo,
            averageSpeed = activity.velocidadMedia,
            calories = activity.calorias,
            maxSpeed = activity.velocidadMaxima,
            speeds = activity.velocidades,
            elevations = activity.desniveles,
            maxAltitude = activity.altitudMaxima,
            route = activity.ruta.map { LatLngRequest(it.latitude, it.longitude) },
            distances = activity.distances,
            title = activity.titulo,
            isPublic = activity.isPublic
        ))
    }

}