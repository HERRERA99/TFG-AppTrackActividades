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
import javax.inject.Inject

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val getPublicPublications: GetPublicPublicationsUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val addLike: AddLikeUseCase,
    private val removeLike: RemoveLikeUseCase,
    val userPreferences: UserPreferences
): ViewModel() {
    val publications:Flow<PagingData<Publication>> = getPublicPublications.execute()

    private val _imagenPerfil = MutableLiveData<String>()
    val imagenPerfil : LiveData<String> = _imagenPerfil

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


    init {
        viewModelScope.launch {
            _imagenPerfil.value = userPreferences.getImagenPerfil()
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
                val token = tokenManager.getToken() ?: throw Exception("No hay token de sesión")
                val comments = getCommentsUseCase(token, publicationId)
                _comments.value = comments
            } catch (e: Exception) {
                _error.value = "Error al cargar comentarios: ${e.message}"
            } finally {
                _isLoadingComments.value = false
            }
        }
    }

    fun isPublicationLiked(likes: List<Int>, publicationId: Long): Boolean {
        return _likedPublications[publicationId] ?: likes.contains(_userId.value)
    }
}