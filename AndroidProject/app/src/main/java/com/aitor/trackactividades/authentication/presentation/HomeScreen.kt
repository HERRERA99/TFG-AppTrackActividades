package com.aitor.trackactividades.authentication.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.aitor.trackactividades.R
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navigateToLogin: () -> Unit,
    navigateToRegister: () -> Unit,
    navigateToFeed: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen de fondo
        Image(
            painter = rememberAsyncImagePainter(R.drawable.home),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Contenido de la pantalla
        Scaffold(
            containerColor = Color.Transparent
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(24.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp, alignment = Alignment.Bottom),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Logo(
                    Modifier
                        .weight(0.8f)
                        .align(Alignment.CenterHorizontally)
                )
                RegisterHomeButton(Modifier.weight(0.1f)) { navigateToRegister() }
                LoginButton(Modifier.weight(0.1f)) { navigateToLogin() }
            }
        }
    }
}

@Composable
fun RegisterHomeButton(modifier: Modifier = Modifier, onRegisterSelected: () -> Unit) {
    Button(
        onClick = { onRegisterSelected() },
        enabled = true,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text(
            text = "Registrarse", color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LoginButton(modifier: Modifier = Modifier, onLoginSelected: () -> Unit) {
    Button(
        onClick = { onLoginSelected() },
        enabled = true,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = "Log in",
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun Logo(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "TrackFit",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "entrena, mejora, aprende",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 24.sp,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.padding(120.dp))
    }
}