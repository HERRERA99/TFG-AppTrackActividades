package com.aitor.trackactividades.quedadas.domain

import com.aitor.trackactividades.quedadas.data.QuedadasRepository
import javax.inject.Inject

class LeaveMeetupUseCase @Inject constructor(private val quedadasRepository: QuedadasRepository) {
    suspend operator fun invoke(id: Long) = quedadasRepository.leaveMeetup(id).toPresentation()
}