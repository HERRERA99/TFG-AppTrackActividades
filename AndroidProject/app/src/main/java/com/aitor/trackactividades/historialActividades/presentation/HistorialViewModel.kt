package com.aitor.trackactividades.historialActividades.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.aitor.trackactividades.core.model.Modalidades
import com.aitor.trackactividades.core.userPreferences.UserPreferences
import com.aitor.trackactividades.feed.data.PublicationsRepository
import com.aitor.trackactividades.feed.presentation.model.Publication
import com.aitor.trackactividades.historialActividades.presentation.model.FiltroModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HistorialViewModel @Inject constructor(
    private val publicationsRepository: PublicationsRepository,
    private val userPreferences: UserPreferences
) : ViewModel(){

    private val _isFiltered = MutableStateFlow<Boolean>(false)
    val isFiltered: StateFlow<Boolean> = _isFiltered

    private val _imagenPerfil =
        MutableLiveData<String>("https://i.postimg.cc/RFSkJZtg/462076-1g-CSN462076-MG3928385-1248x702.webp")
    val imagenPerfil: LiveData<String> = _imagenPerfil

    private val _userId = MutableLiveData<Int?>()
    val userId: LiveData<Int?> = _userId

    data class FilterState(
        val title: String = "",
        val modalidad: Modalidades? = null,
        val distanceRange: ClosedFloatingPointRange<Float> = 0f..80f,
        val elevationRange: ClosedFloatingPointRange<Float> = 0.0f..600.0f,
        val durationRange: ClosedFloatingPointRange<Float> = 0f..21600000f,
        val speedRange: ClosedFloatingPointRange<Float> = 0f..50f
    )

    private val _filterState = MutableStateFlow<FilterState>(FilterState())
    val filterState: StateFlow<FilterState> = _filterState

    init {
        viewModelScope.launch {
            _imagenPerfil.value = userPreferences.getImagenPerfil()!!
            _userId.value = userPreferences.getId()
        }
    }

    fun updateLocalFilterState(update: FilterState) {
        _filterState.value = update
    }

    companion object {
        val DEFAULT_FILTER = FiltroModel(
            nombre = null,
            activityType = null,
            distanciaMin = 0f,
            distanciaMax = 80000f,
            positiveElevationMin = 0.0,
            positiveElevationMax = 600.0,
            durationMin = 0L,
            durationMax = 21600000L,
            averageSpeedMin = 0f,
            averageSpeedMax = 50f
        )
    }

    private val refreshTrigger = MutableStateFlow(DEFAULT_FILTER)

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredPublications: Flow<PagingData<Publication>> = refreshTrigger.flatMapLatest { filtro ->
        publicationsRepository.getPublicationsFiltered(filtro.toRequest())
    }

    // Actualiza el filtro y dispara la recarga
    fun updateFilter(
        nombre: String? = refreshTrigger.value.nombre,
        activityType: Modalidades? = refreshTrigger.value.activityType,
        distanciaMin: Float = refreshTrigger.value.distanciaMin,
        distanciaMax: Float = refreshTrigger.value.distanciaMax,
        positiveElevationMin: Double = refreshTrigger.value.positiveElevationMin,
        positiveElevationMax: Double = refreshTrigger.value.positiveElevationMax,
        durationMin: Long = refreshTrigger.value.durationMin,
        durationMax: Long = refreshTrigger.value.durationMax,
        averageSpeedMin: Float = refreshTrigger.value.averageSpeedMin,
        averageSpeedMax: Float = refreshTrigger.value.averageSpeedMax
    ) {
        refreshTrigger.value = FiltroModel(
            nombre = if (nombre.isNullOrBlank()) null else nombre,
            activityType = activityType,
            distanciaMin = distanciaMin*1000,
            distanciaMax = distanciaMax*1000,
            positiveElevationMin = positiveElevationMin,
            positiveElevationMax = positiveElevationMax,
            durationMin = durationMin,
            durationMax = durationMax,
            averageSpeedMin = averageSpeedMin,
            averageSpeedMax = averageSpeedMax
        )
        _isFiltered.value = true
        Log.d("HistorialViewModel", "updateFilter: ${refreshTrigger.value}")
    }

    // Resetea al filtro por defecto
    fun resetFilter() {
        refreshTrigger.value = DEFAULT_FILTER
        _isFiltered.value = false
    }

    fun formatDurationRange(start: Float, end: Float): String {
        fun format(ms: Float): String {
            val hours = (ms / 3600000).toInt()
            val minutes = ((ms % 3600000) / 60000).toInt()
            return String.format(Locale.US, "%02d:%02d", hours, minutes)
        }

        val startFormatted = format(start)
        val endFormatted = if (end >= 21600000f) "< 06:00" else format(end)

        return "$startFormatted - $endFormatted"
    }

}