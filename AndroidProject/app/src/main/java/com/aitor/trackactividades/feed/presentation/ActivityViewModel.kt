package com.aitor.trackactividades.feed.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitor.trackactividades.core.token.TokenManager
import com.aitor.trackactividades.feed.domain.GetPublicationUseCase
import com.aitor.trackactividades.feed.presentation.model.Publication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val getPublicationUseCase: GetPublicationUseCase
) : ViewModel() {

    private val _publication = MutableStateFlow<Publication?>(null)
    val publication: StateFlow<Publication?> = _publication

    private val _id = MutableStateFlow<Long?>(null)
    val id: StateFlow<Long?> = _id

    fun loadPublication(publicationId: Long) {
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken() ?: return@launch
                _id.value = publicationId
                _publication.value = getPublicationUseCase(token, publicationId)
                Log.d("ActivityViewModel", "Publication loaded: ${_publication.value}")
            } catch (e: Exception) {
                Log.e("ActivityViewModel", "Error loading publication", e)
            }
        }
    }

    fun calcularDistanciasAcumuladas(
        velocidades: List<Float>,
        distanciaTotal: Float,
        intervaloMedicionSegundos: Int = 1 // Asume 1 segundo por defecto
    ): List<Float> {
        if (velocidades.isEmpty()) return emptyList()

        val distancias = mutableListOf<Float>()
        var distanciaAcumulada = 0f

        // Calcular distancias parciales
        for (velocidad in velocidades) {
            val distanciaParcial = velocidad * intervaloMedicionSegundos / 3600
            distanciaAcumulada += distanciaParcial
            distancias.add(distanciaAcumulada)
        }

        // Normalizar para que la última distancia coincida con la distancia total
        if (distanciaAcumulada > 0 && distanciaTotal > 0) {
            val factorCorreccion = distanciaTotal / distanciaAcumulada
            return distancias.map { it * factorCorreccion }
        }

        return distancias
    }
}