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

@HiltViewModel
class DetallesQuedadaViewModel @Inject constructor(
    private val getMeetupUseCase: GetMeetupUseCase,
    private val userPreferences: UserPreferences
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
        val uri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "Google Maps no está instalado", Toast.LENGTH_LONG).show()
        }
    }


}