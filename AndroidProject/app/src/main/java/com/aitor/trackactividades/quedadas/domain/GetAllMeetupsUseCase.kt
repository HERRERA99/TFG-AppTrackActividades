package com.aitor.trackactividades.quedadas.domain

import com.aitor.trackactividades.quedadas.data.QuedadasRepository
import javax.inject.Inject

class GetAllMeetupsUseCase @Inject constructor(private val quedadasRepository: QuedadasRepository) {
    suspend fun execute(lat: Double, lng: Double) = quedadasRepository.getAllMeetups(lat, lng)
}