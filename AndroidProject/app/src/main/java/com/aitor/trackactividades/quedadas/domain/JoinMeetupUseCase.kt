package com.aitor.trackactividades.quedadas.domain

import com.aitor.trackactividades.quedadas.data.QuedadasRepository
import javax.inject.Inject

class JoinMeetupUseCase @Inject constructor(private val quedadasRepository: QuedadasRepository) {
    suspend operator fun invoke(id: Long) = quedadasRepository.joinMeetup(id).toPresentation()
}