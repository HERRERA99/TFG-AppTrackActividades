package com.aitor.trackactividades.authentication.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitor.trackactividades.authentication.domain.ValidateTokenUseCase
import com.aitor.trackactividades.core.token.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val tokenManager: TokenManager,
    private val validateTokenUseCase: ValidateTokenUseCase
): ViewModel() {

    suspend fun isRegistered(): Boolean {
        val token = tokenManager.getToken() ?: return false
        return try {
            val response = validateTokenUseCase("Bearer $token")
            Log.d("TokenValidation", "Response: $response")
            response.isValidate
        } catch (e: Exception) {
            Log.e("TokenValidation", "Error validating token", e)
            false
        }
    }
}