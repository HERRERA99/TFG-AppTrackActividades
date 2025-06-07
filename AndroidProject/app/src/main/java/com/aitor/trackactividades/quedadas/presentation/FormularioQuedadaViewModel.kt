package com.aitor.trackactividades.quedadas.presentation

import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitor.trackactividades.core.model.Modalidades
import com.aitor.trackactividades.quedadas.domain.CreateMeetupUseCase
import com.aitor.trackactividades.quedadas.presentation.Meetup.Meetup
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class FormularioQuedadaViewModel @Inject constructor(
    private val createMeetupUseCase: CreateMeetupUseCase
) : ViewModel() {
    private var _titulo = MutableLiveData<String>()
    val titulo: LiveData<String> = _titulo

    private var _descripcion = MutableLiveData<String>()
    val descripcion: LiveData<String> = _descripcion

    private var _fechaHora = MutableLiveData<LocalDateTime?>()
    val fechaHora: LiveData<LocalDateTime?> = _fechaHora

    private var _localizacion = MutableLiveData<String>("")
    val localizacion: LiveData<String> = _localizacion

    private var _latLng = MutableLiveData<LatLng?>(null)
    val latLng: LiveData<LatLng?> = _latLng

    private var _maxParticipantes = MutableLiveData<Int?>()
    val maxParticipantes: LiveData<Int?> = _maxParticipantes

    private var _modalidad = MutableLiveData(Modalidades.CICLISMO_CARRETERA)
    val modalidad: LiveData<Modalidades> = _modalidad

    private val _mostrarMapa = MutableLiveData(false)
    val mostrarMapa: LiveData<Boolean> = _mostrarMapa

    private val _gpxFile = MutableLiveData<Uri?>(null)
    val gpxFile: LiveData<Uri?> = _gpxFile

    // Función para mostrar/ocultar el mapa
    fun toggleMostrarMapa(mostrar: Boolean) {
        _mostrarMapa.value = mostrar
    }

    // Función para actualizar la ubicación seleccionada
    fun actualizarUbicacionSeleccionada(latLng: LatLng, direccion: String) {
        _latLng.value = latLng
        _localizacion.value = direccion
    }

    fun actualizarTitulo(nuevoTitulo: String) {
        _titulo.value = nuevoTitulo
    }

    fun actualizarDescripcion(nuevaDescripcion: String) {
        _descripcion.value = nuevaDescripcion
    }

    fun actualizarFechaHora(nuevaFechaHora: LocalDateTime) {
        _fechaHora.value = nuevaFechaHora
    }

    fun actualizarMaxParticipantes(nuevoMaxParticipantes: Int?) {
        _maxParticipantes.value = nuevoMaxParticipantes
    }

    fun actualizarModalidad(nuevaModalidad: Modalidades) {
        _modalidad.value = nuevaModalidad
    }

    fun selectGpxFile(uri: Uri?) {
        _gpxFile.value = uri
    }

    fun guardarQuedada(context: Context) {
        viewModelScope.launch {
            val result = createMeetupUseCase(
                title = _titulo.value!!,
                description = _descripcion.value!!,
                dateTime = _fechaHora.value!!,
                location = _localizacion.value!!,
                maxParticipants = _maxParticipantes.value,
                locationCoordinates = _latLng.value!!,
                sportType = _modalidad.value!!,
                gpxUri = _gpxFile.value,
                context = context
            )

            result.fold(
                onSuccess = {
                    Toast.makeText(context, "Quedada creada con éxito ✅", Toast.LENGTH_LONG).show()
                },
                onFailure = { exception ->
                    val errorMessage = when {
                        exception.message?.contains("El archivo GPX no es válido") == true ->
                            "❌ El archivo GPX no es válido. Por favor, sube un archivo correcto."
                        exception.message?.contains("Token no disponible") == true ->
                            "🔒 Sesión expirada. Vuelve a iniciar sesión."
                        else -> "⚠️ Error al crear la quedada: ${exception.message}"
                    }
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    // Función en el ViewModel para obtener la dirección
    fun obtenerDireccionDesdeCoordenadas(latLng: LatLng, context: Context) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            addresses?.firstOrNull()?.let { address ->
                val direccion =
                    address.getAddressLine(0) ?: "${latLng.latitude}, ${latLng.longitude}"
                actualizarUbicacionSeleccionada(latLng, direccion)
            }
        } catch (e: Exception) {
            actualizarUbicacionSeleccionada(latLng, "${latLng.latitude}, ${latLng.longitude}")
        }
    }

    fun limpiarUbicacionSeleccionada() {
        _latLng.value = null
        _localizacion.value = ""
    }

    fun camposObligatoriosCompletos(): Boolean {
        return !_titulo.value.isNullOrEmpty() &&
                _fechaHora.value != null &&
                !_localizacion.value.isNullOrEmpty()
    }
}