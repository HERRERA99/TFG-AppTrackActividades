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
import com.aitor.trackactividades.core.model.Gender
import com.aitor.trackactividades.authentication.presentation.model.RegisterModel
import com.aitor.trackactividades.core.token.TokenManager
import com.aitor.trackactividades.core.userPreferences.UserPreferences
import com.aitor.trackactividades.perfil.domain.GetMyUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val getUserUseCase: GetMyUserUseCase,
    private val userPreferences: UserPreferences,
    private val tokenManager: TokenManager

) : ViewModel() {
    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password1 = MutableLiveData<String>()
    val password1: LiveData<String> = _password1

    private val _password2 = MutableLiveData<String>()
    val password2: LiveData<String> = _password2

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _firstname = MutableLiveData<String>()
    val firstname: LiveData<String> = _firstname

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _lastname = MutableLiveData<String>()
    val lastname: LiveData<String> = _lastname

    private val _birthDate = MutableLiveData<LocalDate>()
    val birthDate: LiveData<LocalDate> = _birthDate

    private val _gender = MutableLiveData<Gender>()
    val gender: LiveData<Gender> = _gender

    private val _navigateToFeed = MutableLiveData<Boolean>()
    val navigateToFeed: LiveData<Boolean> = _navigateToFeed

    private val _textoInfo = MutableLiveData<String>()
    val textoInfo: LiveData<String> = _textoInfo

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _weight = MutableLiveData<Double>()
    val weight: LiveData<Double> = _weight

    private val _height = MutableLiveData<Int>()
    val height: LiveData<Int> = _height

    @RequiresApi(Build.VERSION_CODES.O)
    fun onRegisterChanged(
        email: String,
        username: String,
        password1: String,
        password2: String,
        name: String,
        surname: String,
        birthDate: LocalDate,
        gender: Gender,
        weight: Double,
        height: Int
    ) {
        _email.value = email
        _password1.value = password1
        _password2.value = password2
        _username.value = username
        _firstname.value = name
        _lastname.value = surname
        _birthDate.value = birthDate
        _gender.value = gender
        _weight.value = weight
        _height.value = height
    }

    fun onRegisterSelected() {
        val email = _email.value
        val password1 = _password1.value
        val password2 = _password2.value
        val username = _username.value
        val name = _firstname.value
        val surname = _lastname.value
        val birthDate = _birthDate.value
        val gender = _gender.value
        val weight = _weight.value
        val height = _height.value

        // Verificar que ningún campo sea nulo o esté vacío
        if (email.isNullOrEmpty() || password1.isNullOrEmpty() || password2.isNullOrEmpty() || username.isNullOrEmpty() ||
            name.isNullOrEmpty() || surname.isNullOrEmpty() || birthDate == null || gender == null || weight == null || height == null
        ) {
            showError("Todos los campos son obligatorios.")
            return
        }

        if (chekTexts(email, username, password1, password2, birthDate)) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    // Realiza la llamada de registro
                    val result = registerUseCase(
                        RegisterModel(
                            username = username,
                            email = email,
                            password = password1,
                            firstname = name,
                            lastname = surname,
                            birthdate = birthDate,
                            weight = weight,
                            height = height,
                            gender = gender
                        )
                    )
                    Log.e("Result", result.toString())
                    // Si el token no es nulo, navega al feed
                    if (result.token != null) {
                        tokenManager.clearToken()
                        tokenManager.saveToken(result.token)
                        val user = getUserUseCase("Bearer ${result.token}")
                        Log.e("Usuario", user.toString())
                        userPreferences.saveUser(user)
                        _navigateToFeed.value = true
                    } else {
                        // Si el token es nulo, muestra un mensaje de error
                        showError("Credenciales incorrectas o acceso denegado.")
                    }
                } catch (e: HttpException) {
                    // Manejo específico para errores HTTP
                    when (e.code()) {
                        403 -> {
                            showError("Acceso denegado. Usuario o email ya en uso.")
                        }
                        else -> {
                            showError("Ocurrió un error en el servidor. Intente de nuevo más tarde.")
                        }
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

    fun chekTexts(
        email: String,
        username: String,
        password1: String,
        password2: String,
        birthDate: LocalDate,
    ): Boolean {
        var result = true
        var texto = ""

        // Validación del email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            texto += "El correo electrónico no es válido. \n"
            result = false
        }

        // Validacion del username
        if (!isUsernameValid(username)) {
            texto += "El nombre de usuario no es válido. \n"
            result = false
        }

        // Validación de las contraseñas
        if (password1.isEmpty() || password2.isEmpty()) {
            texto += "Las contraseñas no pueden estar vacías. \n"
            result = false
        } else if (password1 != password2) {
            texto += "Las contraseñas no coinciden. \n"
            result = false
        } else if (!isPasswordSecure(password1)) {
            texto += "Formato de contraseña incorrecto"
            result = false
        }

        // Validación de la fecha de nacimiento
        if (birthDate.isAfter(LocalDate.now())) {
            texto += "La fecha de nacimiento no puede ser futura. \n"
            result = false
        }

        // Asignar el mensaje de error
        _textoInfo.value = texto

        return result
    }

    /**
     * Valida que la contraseña sea segura.
     * Requisitos:
     * - Al menos 8 caracteres.
     * - Al menos una letra mayúscula.
     * - Al menos una letra minúscula.
     * - Al menos un número.
     */
    private fun isPasswordSecure(password: String): Boolean {
        val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$"
        return password.matches(passwordRegex.toRegex())
    }

    /**
     * Valida que el nombre de usuario cumpla con las reglas:
     * - No contiene espacios.
     * - Solo permite letras, números, guiones bajos (_) y guiones (-).
     * - Longitud mínima de 3 caracteres y máxima de 20 (opcional).
     */
    private fun isUsernameValid(username: String): Boolean {
        val usernameRegex = "^[a-zA-Z0-9_-]{3,20}$"
        return username.matches(usernameRegex.toRegex())
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