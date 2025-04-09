package com.aitor.trackactividades.feed.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun ActivityScreen(
    activityViewModel: ActivityViewModel,
    navigateToHome: () -> Unit
) {
    val id by activityViewModel.id.collectAsState()

    Scaffold { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Text(text = "Contenido de la actividad con ID: $id")
        }
    }
}