package com.aitor.trackactividades.quedadas.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val getMeetupUseCase: GetMeetupUseCase
) : ViewModel() {

    private val _meetup = MutableStateFlow<Meetup?>(null)
    val meetup: StateFlow<Meetup?> = _meetup

    fun loadQuedada(quedadaId: Long) {
        viewModelScope.launch {
            val meetup = getMeetupUseCase(quedadaId)
            _meetup.value = meetup
            Log.d("DetallesQuedadaViewModel", "Quedada cargada: $meetup")
        }
    }

}