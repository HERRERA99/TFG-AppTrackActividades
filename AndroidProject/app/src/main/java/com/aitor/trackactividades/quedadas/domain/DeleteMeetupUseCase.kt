package com.aitor.trackactividades.quedadas.domain

import com.aitor.trackactividades.quedadas.data.QuedadasRepository
import javax.inject.Inject

class DeleteMeetupUseCase @Inject constructor(private val quedadasRepository: QuedadasRepository) {
    suspend operator fun invoke(id: Long) = quedadasRepository.deleteMeetup(id)
}