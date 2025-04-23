package com.aitor.trackactividades.perfil.presentation

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitor.trackactividades.core.userPreferences.UserPreferences
import com.aitor.trackactividades.feed.presentation.model.Comment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class PostInteractionViewModel @Inject constructor(
    private val postHandler: PostInteractionHandler,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _comentario = MutableLiveData<String>()
    val comentario: LiveData<String> = _comentario

    private val _imagenPerfil = MutableLiveData<String>()
    val imagenPerfil: LiveData<String> = _imagenPerfil

    private val _likedPublications = mutableStateMapOf<Long, Boolean>()
    val likedPublications: SnapshotStateMap<Long, Boolean> = _likedPublications

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    private val _isLoadingComments = MutableStateFlow(false)
    val isLoadingComments: StateFlow<Boolean> = _isLoadingComments

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _userId = MutableStateFlow<Int?>(null)
    val userId: StateFlow<Int?> = _userId

    init {
        viewModelScope.launch {
            _userId.value = userPreferences.getId()
            _imagenPerfil.value = userPreferences.getImagenPerfil()
        }
    }

    fun toggleLike(publicationId: Long, isLiked: Boolean) {
        viewModelScope.launch() {
            val result = postHandler.toggleLike(publicationId, isLiked)
            result
                .onSuccess { _likedPublications[publicationId] = it }
                .onFailure {
                    _error.value = "Error al dar like: ${it.message}"
                    _likedPublications[publicationId] = !isLiked
                }
        }
    }

    fun loadComments(publicationId: Long) {
        viewModelScope.launch {
            _isLoadingComments.value = true
            val result = postHandler.getComments(publicationId)
            result
                .onSuccess { _comments.value = it }
                .onFailure { _error.value = "Error al cargar comentarios: ${it.message}" }
            _isLoadingComments.value = false
        }
    }

    fun addComment(publicationId: Long) {
        viewModelScope.launch {
            val result = postHandler.addComment(publicationId, _comentario.value ?: "")
            result
                .onSuccess {
                    _comments.value += it
                    clearComentario()
                }
                .onFailure { _error.value = "Error al agregar comentario: ${it.message}" }
        }
    }

    fun onComentarioChange(value: String) {
        _comentario.value = value
    }

    fun clearComentario() {
        _comentario.value = ""
    }

    fun isPublicationLiked(likes: List<Int>, publicationId: Long): Boolean {
        return _likedPublications[publicationId] ?: likes.contains(_userId.value)
    }

    fun tiempoTranscurrido(fecha: LocalDateTime): String {
        return postHandler.tiempoTranscurrido(fecha)
    }
}