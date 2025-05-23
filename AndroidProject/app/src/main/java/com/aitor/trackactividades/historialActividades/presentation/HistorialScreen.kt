package com.aitor.trackactividades.historialActividades.presentation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.aitor.trackactividades.feed.presentation.FeedBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    historialViewModel: HistorialViewModel,
    navigateToStartRecordActivity: () -> Unit,
    navigateToActivity: (Long) -> Unit,
    navigateToFeed: () -> Unit
) {
    val context = LocalContext.current

    // Estado para rastrear si los permisos han sido concedidos
    var hasLocationPermissions by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Launcher para solicitar permisos
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasFineLocationPermission =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val hasCoarseLocationPermission =
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        hasLocationPermissions = hasFineLocationPermission || hasCoarseLocationPermission

        if (hasLocationPermissions) {
            // Navegar a la pantalla de registro si los permisos han sido concedidos
            navigateToStartRecordActivity()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Historial",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = { /* Lógica para abrir filtros */ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtrar",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            FeedBottomBar(
                onRegisterClick = {
                    if (hasLocationPermissions) {
                        navigateToStartRecordActivity()
                    } else {
                        launcher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                },
                onHistorialClick = null,
                onFeedClick = {
                    navigateToFeed()
                }
            )
        }
    ) { innerPadding ->
        Body(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun Body(modifier: Modifier) {

}

