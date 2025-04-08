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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val getPublicPublications: GetPublicPublicationsUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val addCommentUseCase: AddCommentUseCase,
    private val addLike: AddLikeUseCase,
    private val removeLike: RemoveLikeUseCase,
    val userPreferences: UserPreferences
) : ViewModel() {
    val publications: Flow<PagingData<Publication>> = getPublicPublications.execute()

    private val _imagenPerfil =
        MutableLiveData<String>("https://i.postimg.cc/RFSkJZtg/462076-1g-CSN462076-MG3928385-1248x702.webp")
    val imagenPerfil: LiveData<String> = _imagenPerfil

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _userId = MutableLiveData<Int?>(null)
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
    }

    suspend fun logout() {
        tokenManager.clearToken()
    }

    /**
     * Tiempo en ms a formato hh:mm:ss
     */
    fun formatDuration(milliseconds: Long): String {
        val secondsTotal = milliseconds / 1000
        val hours = secondsTotal / 3600
        val minutes = (secondsTotal % 3600) / 60
        val seconds = secondsTotal % 60

        return buildString {
            if (hours > 0) append("${hours}h ")
            if (minutes > 0 || hours > 0) append("${minutes}m ")
            append("${seconds}s")
        }.trim()
    }

    fun formatDistance(distance: Float): String {
        return if (distance < 1000) {
            "${distance.toInt()} m"
        } else {
            "%.2f km".format(distance / 1000)
        }
    }

    fun toggleLike(publicationId: Long, isLiked: Boolean) {
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken() ?: throw Exception("No hay token de sesión")
                val userId = userPreferences.getId() ?: throw Exception("No hay userId")

                if (isLiked) {
                    removeLike(token, publicationId, userId)
                    _likedPublications[publicationId] = false
                } else {
                    addLike(token, publicationId, userId)
                    _likedPublications[publicationId] = true
                }
            } catch (e: Exception) {
                _error.value = "Error al dar like: ${e.message}"
                Log.e("FeedViewModel", "Error al dar like", e)
                // Revertir el cambio en caso de error
                _likedPublications[publicationId] = !isLiked
            }
        }
    }

    fun loadComments(publicationId: Long) {
        viewModelScope.launch {
            _isLoadingComments.value = true
            try {
                _comments.value = emptyList()
                val token = tokenManager.getToken() ?: throw Exception("No hay token de sesión")
                val comments = getCommentsUseCase(token, publicationId)
                _comments.value = comments
                Log.d("FeedViewModel", "Comentarios cargados: $comments")
            } catch (e: Exception) {
                _error.value = "Error al cargar comentarios: ${e.message}"
                Log.d("Error", "Error al cargar comentarios: ${e.message}")
            } finally {
                _isLoadingComments.value = false
            }
        }
    }

    fun isPublicationLiked(likes: List<Int>, publicationId: Long): Boolean {
        return _likedPublications[publicationId] ?: likes.contains(_userId.value)
    }

    fun onComentarioChange(comentario: String) {
        _comentario.value = comentario
    }

    fun clearComentario() {
        _comentario.value = ""
    }

    fun addComment(id: Long) {
        viewModelScope.launch {
            Log.d("FeedViewModel", "Agregando comentario: ${comentario.value}")
            try {
                val comment = addCommentUseCase.invoke(
                    tokenManager.getToken()!!,
                    id,
                    _userId.value!!,
                    comentario.value!!
                )
                _comments.value = _comments.value + comment
                clearComentario()
                Log.d("FeedViewModel", "Comentario agregado: $comment")
                Log.d("FeedViewModel", "Comentarios actualizados: ${_comments.value}")
            } catch (e: Exception) {
                _error.value = "Error al agregar comentario: ${e.message}"
            }
        }
    }

    fun tiempoTranscurrido(fecha: LocalDateTime): String {
        val ahora = LocalDateTime.now()
        val duracion = Duration.between(fecha, ahora)

        return when {
            duracion.toMinutes() < 1 -> "justo ahora"
            duracion.toMinutes() < 60 -> "hace ${duracion.toMinutes()} minuto(s)"
            duracion.toHours() < 24 -> "hace ${duracion.toHours()} hora(s)"
            duracion.toDays() == 1L -> "ayer"
            duracion.toDays() < 7 -> "hace ${duracion.toDays()} día(s)"
            duracion.toDays() < 30 -> "hace ${duracion.toDays() / 7} semana(s)"
            duracion.toDays() < 365 -> "hace ${duracion.toDays() / 30} mes(es)"
            else -> "hace ${duracion.toDays() / 365} año(s)"
        }
    }
}