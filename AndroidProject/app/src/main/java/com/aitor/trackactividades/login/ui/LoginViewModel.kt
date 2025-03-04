package com.aitor.trackactividades.login.ui

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(): ViewModel() {
    private val _email = MutableLiveData<String>()
    val email : LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password : LiveData<String> = _password

    private val _isLoginEnable = MutableLiveData<Boolean>()
    val isLoginEnable : LiveData<Boolean> = _isLoginEnable

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading : LiveData<Boolean> = _isLoading

    private val _navigateToFeed = MutableLiveData<Boolean>()
    val navigateToFeed: LiveData<Boolean> = _navigateToFeed

    fun onLoginChanged(email: String, password: String) {
        _email.value = email
        _password.value = password
        _isLoginEnable.value = enableLogin(email, password)
    }

    fun enableLogin(email: String, password: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length > 6
    }

    fun onLoginSelected() {
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