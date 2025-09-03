package com.aitor.trackactividades.quedadas.domain

import com.aitor.trackactividades.quedadas.data.QuedadasRepository
import javax.inject.Inject

class GetMyMeetupsUseCase @Inject constructor(private val quedadasRepository: QuedadasRepository) {
    suspend fun execute() = quedadasRepository.getMyMeetups()
}