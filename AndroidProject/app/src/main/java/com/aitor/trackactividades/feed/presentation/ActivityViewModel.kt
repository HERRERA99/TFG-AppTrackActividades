package com.aitor.trackactividades.feed.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitor.trackactividades.core.token.TokenManager
import com.aitor.trackactividades.feed.domain.GetPublicationUseCase
import com.aitor.trackactividades.feed.presentation.model.Publication
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.axis.Axis.Position.Vertical
import com.patrykandpatrick.vico.core.common.shape.Shape
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

    fun formatSeconds(
        context: CartesianMeasuringContext,
        x: Double,
        vertical: Vertical?
    ): String {
        val totalSeconds = x.toInt()

        return when {
            totalSeconds >= 3600 -> {
                val hours = totalSeconds / 3600
                val remainingMinutes = (totalSeconds % 3600) / 60
                val remainingSeconds = totalSeconds % 60

                if (remainingMinutes > 0 || remainingSeconds > 0) {
                    "${hours}h ${remainingMinutes}min ${remainingSeconds}s"
                } else {
                    "${hours}h"
                }
            }
            totalSeconds >= 60 -> {
                val minutes = totalSeconds / 60
                val remainingSeconds = totalSeconds % 60

                if (remainingSeconds > 0) {
                    "${minutes}min ${remainingSeconds}s"
                } else {
                    "${minutes}min"
                }
            }
            else -> "${totalSeconds}s"
        }
    }

    fun RoundedShape(cornerRadius: Float): Shape = Shape { _, path, left, top, right, bottom ->
        val width = right - left
        val height = bottom - top
        val radius = cornerRadius.coerceAtMost(minOf(width, height) / 2f)

        path.reset()
        path.moveTo(left + radius, top)
        path.lineTo(right - radius, top)
        path.quadTo(right, top, right, top + radius)
        path.lineTo(right, bottom - radius)
        path.quadTo(right, bottom, right - radius, bottom)
        path.lineTo(left + radius, bottom)
        path.quadTo(left, bottom, left, bottom - radius)
        path.lineTo(left, top + radius)
        path.quadTo(left, top, left + radius, top)
        path.close()
    }

}