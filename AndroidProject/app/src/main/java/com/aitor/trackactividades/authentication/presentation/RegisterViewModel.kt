package com.aitor.trackactividades.authentication.presentation

import android.os.Build
import android.util.Log
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitor.trackactividades.authentication.domain.RegisterUseCase
import com.aitor.trackactividades.authentication.presentation.model.LoginModel
import com.aitor.trackactividades.authentication.presentation.model.RegisterModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password1 = MutableLiveData<String>()
    val password1: LiveData<String> = _password1

    private val _password2 = MutableLiveData<String>()
    val password2: LiveData<String> = _password2

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _surname = MutableLiveData<String>()
    val surname: LiveData<String> = _surname

    private val _birthDate = MutableLiveData<LocalDate>()
    val birthDate: LiveData<LocalDate> = _birthDate

    private val _gender = MutableLiveData<String>()
    val gender: LiveData<String> = _gender

    private val _navigateToFeed = MutableLiveData<Boolean>()
    val navigateToFeed: LiveData<Boolean> = _navigateToFeed

    private val _textoInfo = MutableLiveData<String>()
    val textoInfo: LiveData<String> = _textoInfo

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun onRegisterChanged(
        email: String,
        username: String,
        password1: String,
        password2: String,
        name: String,
        surname: String,
        birthDate: LocalDate,
        gender: String
    ) {
        _email.value = email
        _password1.value = password1
        _password2.value = password2
        _username.value = username
        _name.value = name
        _surname.value = surname
        _birthDate.value = birthDate
        _gender.value = gender
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onRegisterSelected() {
        if (chekTexts(
                _email.value!!,
                _password1.value!!,
                _username.value!!,
                _name.value!!,
                _surname.value!!,
                _birthDate.value!!,
                _gender.value!!
            )
        ) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    // Realiza la llamada de login
                    val result = registerUseCase(RegisterModel(
                        _email.value!!,
                        _password1.value!!,
                        _username.value!!,
                        _name.value!!,
                        _surname.value!!,
                        _birthDate.value!!,
                        _gender.value!!
                    ))
                    // Si el token no es nulo, navega al feed
                    if (result.token != null) {
                        _navigateToFeed.value = true
                    } else {
                        // Si el token es nulo, muestra un mensaje de error (opcional)
                        showError("Credenciales incorrectas o acceso denegado.")
                    }
                } catch (e: HttpException) {
                    // Manejo específico para errores HTTP, como el error 403
                    if (e.code() == 403) {
                        showError("Acceso denegado. Verifique sus credenciales.")
                    } else {
                        showError("Ocurrió un error en el servidor. Intente de nuevo más tarde.")
                    }
                } catch (e: Exception) {
                    // Manejo de cualquier otro tipo de excepción (red, timeout, etc.)
                    showError("Ocurrió un error. Intente de nuevo.")
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun chekTexts(
        email: String?,
        password1: String?,
        password2: String?,
        name: String?,
        surname: String?,
        birthDate: LocalDate?,
        gender: String?
    ): Boolean {
        var result = true
        var texto = ""

        // Validación del email
        if (email.isNullOrEmpty()) {
            texto += "Email vacío \n"
            result = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            texto += "Email incorrecto \n"
            result = false
        }

        // Validación de las contraseñas
        if (password1.isNullOrEmpty() || password2.isNullOrEmpty()) {
            texto += "Contraseñas vacías \n"
            result = false
        } else if (password1 != password2) {
            texto += "Las contraseñas no coinciden \n"
            result = false
        } else if (password1.length < 6) {
            texto += "La contraseña debe tener al menos 6 caracteres \n"
            result = false
        }

        // Validación del nombre
        if (name.isNullOrEmpty()) {
            texto += "El nombre está vacío \n"
            result = false
        }

        // Validación del apellido
        if (surname.isNullOrEmpty()) {
            texto += "El apellido está vacío \n"
            result = false
        }

        // Validación de la fecha de nacimiento
        if (birthDate == null) {
            texto += "La fecha de nacimiento está vacía \n"
            result = false
        } else if (birthDate.isAfter(LocalDate.now())) {
            texto += "La fecha de nacimiento no puede ser futura \n"
            result = false
        }

        // Validación del género
        if (gender.isNullOrEmpty()) {
            texto += "El género está vacío \n"
            result = false
        }

        // Asignar el mensaje de error
        _textoInfo.value = texto

        return result
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