package com.aitor.trackactividades.recordActivity.presentation

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordStartActivityScreen(
    recordActivityViewModel: RecordActivityViewModel,
    navigateToRecordActivity: () -> Unit,
    navigateToFeed: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var userLocation by remember { mutableStateOf<Location?>(null) }

    // Verificar permisos de ubicación
    val hasFineLocationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    // Obtener la ubicación del usuario si los permisos están concedidos
    LaunchedEffect(Unit) {
        if (hasFineLocationPermission || hasCoarseLocationPermission) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                val location = fusedLocationClient.lastLocation.await()
                userLocation = location
            } catch (e: Exception) {
                Log.e("Location", "Error obteniendo la ubicación", e)
            }
        } else {
            Log.w("Location", "Permisos de ubicación no concedidos")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navigateToFeed() }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { navigateToRecordActivity() },
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            GoogleMapView(userLocation)
        }
    }
}

@Composable
fun GoogleMapView(userLocation: Location?) {
    val cameraPositionState = rememberCameraPositionState()
    val routeCoordinates: List<LatLng> = listOf(
        LatLng(43.3845, -4.0512), // Punto de inicio
        LatLng(43.3848, -4.0515), // Primer tramo
        LatLng(43.3850, -4.0520), // Segundo tramo
        LatLng(43.3855, -4.0525), // Tercer tramo
        LatLng(43.3860, -4.0530), // Cuarto tramo
        LatLng(43.3865, -4.0535), // Quinto tramo
        LatLng(43.3870, -4.0540)  // Punto final
    )

    // Centrar la cámara en la ubicación del usuario cuando esté disponible
    LaunchedEffect(userLocation) {
        if (userLocation != null) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(userLocation.latitude, userLocation.longitude),
                15f // Nivel de zoom
            )
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = true // Habilitar la capa de ubicación del usuario
        ),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = true // Habilitar el botón de ubicación
        )
    ) {
        Polyline(
            points = routeCoordinates,
            color = MaterialTheme.colorScheme.primary,
            width = 8f
        )
    }
}