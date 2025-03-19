package com.aitor.trackactividades.authentication.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    navigateToHome: () -> Unit,
    navigateToFeed: () -> Unit,
    splashViewModel: SplashViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var isUserRegistered by remember { mutableStateOf<Boolean?>(null) }
    var hasNavigated by remember { mutableStateOf(false) } // Evita el bucle infinito

    // Verificamos si hay un usuario registrado al iniciar la pantalla
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isUserRegistered = splashViewModel.isRegistered()
        }
    }

    // Si el usuario ya está registrado y no hemos navegado aún, navegar al feed
    LaunchedEffect(isUserRegistered) {
        if (isUserRegistered == true && !hasNavigated) {
            hasNavigated = true
            navigateToFeed()
        }
        // Si el usuario no está registrado y no hemos navegado aún, navegar al home
        else if (isUserRegistered == false && !hasNavigated) {
            hasNavigated = true
            navigateToHome()
        }
    }

    Scaffold() { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Cargando...",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}