package com.aitor.trackactividades.authentication.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitor.trackactividades.authentication.domain.ActualizarFcmTokenUseCase
import com.aitor.trackactividades.authentication.domain.LoginUseCase
import com.aitor.trackactividades.authentication.presentation.model.LoginModel
import com.aitor.trackactividades.core.token.TokenManager
import com.aitor.trackactividades.core.userPreferences.UserPreferences
import com.aitor.trackactividades.perfil.domain.GetMyUserUseCase
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getUserUseCase: GetMyUserUseCase,
    private val userPreferences: UserPreferences,
    private val actualizarFcmTokenUseCase: ActualizarFcmTokenUseCase,
    private val tokenManager: TokenManager
): ViewModel() {
    private val _identifier = MutableLiveData<String>()
    val identifier : LiveData<String> = _identifier

    private val _password = MutableLiveData<String>()
    val password : LiveData<String> = _password

    private val _isLoginEnable = MutableLiveData<Boolean>()
    val isLoginEnable : LiveData<Boolean> = _isLoginEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _navigateToFeed = MutableLiveData<Boolean>()
    val navigateToFeed: LiveData<Boolean> = _navigateToFeed

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun onLoginChanged(email: String, password: String) {
        _identifier.value = email
        _password.value = password
        _isLoginEnable.value = enableLogin(email, password)
    }

    fun enableLogin(identifier: String, password: String): Boolean {
        return identifier.isNotBlank() && password.isNotBlank()
    }

    fun onLoginSelected() {
        val identifier = _identifier.value?.takeIf { it.isNotBlank() } ?: return
        val pass = _password.value?.takeIf { it.isNotBlank() } ?: return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = loginUseCase(LoginModel(identifier, pass))
                if (result.token != null) {
                    tokenManager.clearToken()
                    tokenManager.saveToken(result.token)

                    // Agregar el prefijo "Bearer " al token
                    val authHeader = "Bearer ${result.token}"

                    // Llamar a getUserUseCase con el token formateado
                    val user = getUserUseCase(authHeader)

                    Log.e("Usuario", user.toString())
                    userPreferences.saveUser(user)
                    _navigateToFeed.value = true

                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val fcmToken = task.result
                            viewModelScope.launch {
                                try {
                                    actualizarFcmTokenUseCase(fcmToken)
                                } catch (e: Exception) {
                                    Log.e("FCM", "Error al enviar token al backend", e)
                                }
                            }
                        } else {
                            Log.e("FCM", "No se pudo obtener el token", task.exception)
                        }
                    }

                } else {
                    showError("Credenciales incorrectas o acceso denegado.")
                }
            } catch (e: HttpException) {
                when (e.code()) {
                    400 -> showError("Verifica la cuenta con el email recibido.")
                    403 -> showError("Acceso denegado. Verifique sus credenciales.")
                    else -> showError("Ocurrió un error en el servidor. Intente de nuevo más tarde.")
                }
            } catch (e: Exception) {
                showError("Ocurrió un error. Intente de nuevo.")
            } finally {
                _isLoading.value = false
            }
        }

    }


    fun showError(message: String) {
        _errorMessage.value = message
        // Resetear el error después de un pequeño delay, esto garantiza que siempre se pueda mostrar el toast para errores consecutivos
        viewModelScope.launch {
            delay(500)  // Un pequeño retraso antes de resetear el mensaje
            _errorMessage.value = null
        }
    }


}