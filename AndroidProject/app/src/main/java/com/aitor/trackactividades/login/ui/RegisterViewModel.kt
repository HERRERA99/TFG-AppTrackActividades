package com.aitor.trackactividades.login.ui

import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor() : ViewModel() {
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

    fun onRegisterChanged(
        email: String,
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
        _name.value = name
        _surname.value = surname
        _birthDate.value = birthDate
        _gender.value = gender
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onRegisterSelected() {
        if (chekTexts(
                _email.value,
                _password1.value,
                _password2.value,
                _name.value,
                _surname.value,
                _birthDate.value,
                _gender.value
            )
        ) {
            _isLoading.value = true

            // Simulación de autenticación exitosa después de un delay
            Thread {
                Thread.sleep(2000) // Simula una operación de red

                _isLoading.postValue(false)

                // Aquí deberías comprobar si el usuario existe en tu backend
                val userExists = true // Simulación

                if (userExists) {
                    _navigateToFeed.postValue(true)
                }
            }.start()
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
}