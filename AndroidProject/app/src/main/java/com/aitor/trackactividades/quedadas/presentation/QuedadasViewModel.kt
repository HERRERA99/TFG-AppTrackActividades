package com.aitor.trackactividades.quedadas.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.aitor.trackactividades.core.location.LocationRepository
import com.aitor.trackactividades.quedadas.domain.GetAllMeetupsUseCase
import com.aitor.trackactividades.quedadas.domain.GetMyMeetupsUseCase
import com.aitor.trackactividades.quedadas.presentation.model.ItemMeetupList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class QuedadasViewModel @Inject constructor(
    private val getAllMeetupsUseCase: GetAllMeetupsUseCase,
    private val locationRepository: LocationRepository,
    private val myMeetupsUseCase: GetMyMeetupsUseCase
) : ViewModel() {
    private val _refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
    val refreshTrigger = _refreshTrigger.asSharedFlow()

    private val _location = MutableStateFlow<Pair<Double, Double>?>(null)
    val location: StateFlow<Pair<Double, Double>?> = _location.asStateFlow()

    // Emitir valor inicial para que myMeetups empiece a cargar
    init {
        viewModelScope.launch {
            _refreshTrigger.emit(Unit) // Emitir valor inicial
            val location = locationRepository.getLastKnownLocation()
            location?.let { (lat, lng) ->
                _location.value = lat to lng
            }
        }
    }

    val meetups: Flow<PagingData<ItemMeetupList>> = location.flatMapLatest { location ->
        if (location != null) {
            getAllMeetupsUseCase.execute(location.first, location.second)
        } else {
            flowOf(PagingData.empty())
        }
    }

    val myMeetups: Flow<PagingData<ItemMeetupList>> = refreshTrigger.flatMapLatest {
        myMeetupsUseCase.execute()
    }

    fun refresh() {
        viewModelScope.launch {
            // Refrescar ambos flujos
            _refreshTrigger.emit(Unit)
            val location = locationRepository.getLastKnownLocation()
            location?.let { (lat, lng) ->
                _location.value = lat to lng
            }
        }
    }

    fun formatDateTime(raw: String): String {
        val dateTime = LocalDateTime.parse(raw)
        val formatter =
            DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy, HH:mm 'h'", Locale("es"))
        return dateTime.format(formatter)
    }

    fun extraerPuebloProvinciaPaisSinNumeros(direccion: String): String {
        val partes = direccion.split(",")
            .map { it.trim().replace(Regex("\\d+"), "").trim() }
            .filter { it.any { c -> c.isLetter() } }

        if (partes.size < 3) return direccion

        val pais = partes[partes.size - 1]
        val provincia = partes[partes.size - 2]
        val pueblo = partes[partes.size - 3]

        return "$pueblo, $provincia, $pais"
    }

}