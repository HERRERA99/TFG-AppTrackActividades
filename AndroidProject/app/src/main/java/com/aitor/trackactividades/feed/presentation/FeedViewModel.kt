package com.aitor.trackactividades.feed.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.aitor.trackactividades.core.token.TokenManager
import com.aitor.trackactividades.core.userPreferences.UserPreferences
import com.aitor.trackactividades.feed.domain.AddCommentUseCase
import com.aitor.trackactividades.feed.domain.AddLikeUseCase
import com.aitor.trackactividades.feed.domain.GetCommentsUseCase
import com.aitor.trackactividades.feed.domain.GetPublicPublicationsUseCase
import com.aitor.trackactividades.feed.domain.RemoveLikeUseCase
import com.aitor.trackactividades.feed.presentation.model.Comment
import com.aitor.trackactividades.feed.presentation.model.Publication
import com.aitor.trackactividades.perfil.presentation.PostInteractionHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    getPublicPublications: GetPublicPublicationsUseCase,
    private val postHandler: PostInteractionHandler,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val publications: Flow<PagingData<Publication>> = refreshTrigger.flatMapLatest {
        getPublicPublications.execute()
    }

    private val _imagenPerfil =
        MutableLiveData<String>("https://i.postimg.cc/RFSkJZtg/462076-1g-CSN462076-MG3928385-1248x702.webp")
    val imagenPerfil: LiveData<String> = _imagenPerfil

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _userId = MutableLiveData<Int?>()
    val userId: LiveData<Int?> = _userId

    private val _likedPublications = mutableStateMapOf<Long, Boolean>()
    val likedPublications: SnapshotStateMap<Long, Boolean> = _likedPublications

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    private val _isLoadingComments = MutableStateFlow(false)
    val isLoadingComments: StateFlow<Boolean> = _isLoadingComments

    private val _comentario = MutableLiveData<String>()
    val comentario: LiveData<String> = _comentario

    init {
        viewModelScope.launch {
            _imagenPerfil.value = userPreferences.getImagenPerfil()!!
            _userId.value = userPreferences.getId()
        }
        refreshPublications()
    }

    fun refreshPublications() {
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
        }
    }

    fun toggleLike(publicationId: Long, isLiked: Boolean) {
        viewModelScope.launch {
            val result = postHandler.toggleLike(publicationId, isLiked)
            result
                .onSuccess {
                    _likedPublications[publicationId] = it
                    refreshPublications()
                }
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

    fun cargarImagenPerfil() {
        viewModelScope.launch {
            _imagenPerfil.value = userPreferences.getImagenPerfil() ?: ""
        }
    }
}
