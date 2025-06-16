package com.aitor.trackactividades.perfil.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
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
import com.aitor.trackactividades.perfil.domain.AddFollowUseCase
import com.aitor.trackactividades.perfil.domain.GetUserByIdUserCase
import com.aitor.trackactividades.perfil.domain.GetUserPublicationsUseCase
import com.aitor.trackactividades.perfil.domain.RemoveFollowUseCase
import com.aitor.trackactividades.perfil.domain.UpdateProfilePictureUseCase
import com.aitor.trackactividades.perfil.presentation.model.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import javax.inject.Inject
import androidx.core.net.toUri
import kotlinx.coroutines.flow.update

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val getUserPublicationsUseCase: GetUserPublicationsUseCase,
    private val getUserByIdUserCase: GetUserByIdUserCase,
    private val addFollowUseCase: AddFollowUseCase,
    private val removeFollowUseCase: RemoveFollowUseCase,
    private val tokenManager: TokenManager,
    private val userPreferences: UserPreferences,
    private val updateProfileUseCase: UpdateProfilePictureUseCase
) : ViewModel() {

    private val _userId = MutableLiveData<Int?>()
    val userId: LiveData<Int?> = _userId

    private val _user = MutableStateFlow<UserModel?>(null)
    val user: StateFlow<UserModel?> = _user

    private val _imagenPerfil =
        MutableLiveData<String>("https://i.postimg.cc/RFSkJZtg/462076-1g-CSN462076-MG3928385-1248x702.webp")
    val imagenPerfil: LiveData<String> = _imagenPerfil

    @OptIn(ExperimentalCoroutinesApi::class)
    val publications: Flow<PagingData<Publication>> = _user
        .filterNotNull()
        .flatMapLatest { getUserPublicationsUseCase.execute(userId = it.id) }

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _isCurrentUser = MutableStateFlow<Boolean>(false)
    val isCurrentUser: StateFlow<Boolean> = _isCurrentUser.asStateFlow()


    fun loadPerfil(idPerfil: Int) {
        viewModelScope.launch {
            _userId.value = userPreferences.getId()
            val userLoaded = getUserByIdUserCase(
                token = tokenManager.getToken() ?: "",
                idUser = idPerfil
            )
            _user.value = userLoaded
            _isCurrentUser.value = _userId.value == userLoaded.id
            _imagenPerfil.value = userPreferences.getImagenPerfil()!!
            Log.d("Perfil", user.value.toString())
        }
    }

    fun onFollowClick() {
        viewModelScope.launch {
            if (user.value != null) {
                if (user.value!!.isFollowing) {
                    removeFollowUseCase(user.value!!.id)
                    _user.value = user.value!!.copy(
                        isFollowing = false,
                        followersCount = user.value!!.followersCount - 1
                    )
                } else {
                    addFollowUseCase(user.value!!.id)
                    _user.value = user.value!!.copy(
                        isFollowing = true,
                        followersCount = user.value!!.followersCount + 1
                    )
                }
            }
        }
    }

    fun onUpdateClick(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val imageFile = uriStringToFile(context, imageUri)
                val updatedUser = updateProfileUseCase(_userId.value!!, imageFile)

                _user.update { currentUser ->
                    currentUser?.copy(image = updatedUser.image)
                }

                userPreferences.updateImageUrl(updatedUser.image)

                Toast.makeText(context, "Se ha actualizado la imagen de perfil", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Error al actualizar la imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun uriStringToFile(context: Context, uriString: Uri): File {
        val uri = uriString
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val tempFile = File.createTempFile("image", ".jpg", context.cacheDir)
        tempFile.outputStream().use { output ->
            inputStream.copyTo(output)
        }
        return tempFile
    }

}
