package com.aitor.trackactividades.quedadas.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitor.trackactividades.core.userPreferences.UserPreferences
import com.aitor.trackactividades.feed.presentation.model.Publication
import com.aitor.trackactividades.quedadas.domain.GetMeetupUseCase
import com.aitor.trackactividades.quedadas.presentation.model.Meetup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.net.toUri
import com.aitor.trackactividades.quedadas.domain.DeleteMeetupUseCase
import com.aitor.trackactividades.quedadas.domain.JoinMeetupUseCase
import com.aitor.trackactividades.quedadas.domain.LeaveMeetupUseCase

@HiltViewModel
class DetallesQuedadaViewModel @Inject constructor(
    private val getMeetupUseCase: GetMeetupUseCase,
    private val userPreferences: UserPreferences,
    private val joinMeetupUseCase: JoinMeetupUseCase,
    private val leaveMeetupUseCase: LeaveMeetupUseCase,
    private val deleteMeetupUseCase: DeleteMeetupUseCase
) : ViewModel() {

    private val _meetup = MutableStateFlow<Meetup?>(null)
    val meetup: StateFlow<Meetup?> = _meetup

    var userId: Int? = null

    init {
        viewModelScope.launch {
            userId = userPreferences.getId()
        }
    }

    fun loadQuedada(quedadaId: Long) {
        viewModelScope.launch {
            val meetup = getMeetupUseCase(quedadaId)
            _meetup.value = meetup
            Log.d("DetallesQuedadaViewModel", "Quedada cargada: $meetup")
        }
    }

    fun isCreator(): Boolean {
        return meetup.value?.organizerId == userId
    }

    fun abrirGoogleMaps(context: Context, lat: Double, lng: Double) {
        val uri = "https://www.google.com/maps/dir/?api=1&destination=$lat,$lng".toUri()
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Google Maps no está instalado", Toast.LENGTH_LONG).show()
        }
    }

    fun onJoinClick(context: Context, id: Long) {
        viewModelScope.launch {
            try {
                val updatedMeetup = joinMeetupUseCase(id)
                _meetup.value = updatedMeetup.copy()
                Toast.makeText(context, "Apuntado a la quedada correctamente", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al apuntarse a la quedada", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onLeaveClick(context: Context, id: Long) {
        viewModelScope.launch {
            try {
                val updatedMeetup = leaveMeetupUseCase(id)
                _meetup.value = updatedMeetup.copy()
                Toast.makeText(context, "Desapuntado de la quedada correctamente", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al desapuntarse de la quedada", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun onDeleteClick(context: Context, id: Long) {
        viewModelScope.launch {
            try {
                deleteMeetupUseCase(id)
                Toast.makeText(context, "Quedada eliminada correctamente", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al eliminar la quedada", Toast.LENGTH_LONG).show()
            }
        }
    }
}