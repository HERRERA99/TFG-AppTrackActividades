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