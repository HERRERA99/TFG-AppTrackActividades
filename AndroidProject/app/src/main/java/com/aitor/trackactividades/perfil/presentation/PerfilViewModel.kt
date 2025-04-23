package com.aitor.trackactividades.perfil.presentation

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
import com.aitor.trackactividades.feed.presentation.model.Comment
import com.aitor.trackactividades.feed.presentation.model.Publication
import com.aitor.trackactividades.perfil.domain.GetUserByIdUserCase
import com.aitor.trackactividades.perfil.domain.GetUserPublicationsUseCase
import com.aitor.trackactividades.perfil.presentation.model.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val getUserPublicationsUseCase: GetUserPublicationsUseCase,
    private val getUserByIdUserCase: GetUserByIdUserCase,
    private val tokenManager: TokenManager,
    private val postHandler: PostInteractionHandler
) : ViewModel() {

    private val _userId = MutableStateFlow<Int?>(null)
    val userId: StateFlow<Int?> = _userId

    private val _user = MutableLiveData<UserModel>()
    val user: LiveData<UserModel> = _user

    @OptIn(ExperimentalCoroutinesApi::class)
    val publications: Flow<PagingData<Publication>> = _userId
        .filterNotNull()
        .flatMapLatest { getUserPublicationsUseCase.execute(userId = it) }

    private val _likedPublications = mutableStateMapOf<Long, Boolean>()
    val likedPublications: SnapshotStateMap<Long, Boolean> = _likedPublications

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    private val _isLoadingComments = MutableStateFlow(false)
    val isLoadingComments: StateFlow<Boolean> = _isLoadingComments

    private val _comentario = MutableLiveData<String>()
    val comentario: LiveData<String> = _comentario

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadPerfil(idPerfil: Int) {
        viewModelScope.launch {
            _userId.value = idPerfil
            _user.value = getUserByIdUserCase(
                token = tokenManager.getToken() ?: "",
                idUser = idPerfil
            )
            Log.d("Perfil", user.value.toString())
        }
    }

    fun toggleLike(publicationId: Long, isLiked: Boolean) {
        viewModelScope.launch {
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

    suspend fun logout() {
        tokenManager.clearToken()
    }

    fun tiempoTranscurrido(fecha: LocalDateTime): String {
        return postHandler.tiempoTranscurrido(fecha)
    }
}
