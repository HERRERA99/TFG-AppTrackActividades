package com.aitor.trackactividades.authentication.presentation

import androidx.lifecycle.ViewModel
import com.aitor.trackactividades.core.token.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager
): ViewModel() {

    suspend fun isRegistered(): Boolean {
        return tokenManager.getToken() != null
    }
}