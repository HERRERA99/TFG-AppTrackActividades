package com.aitor.trackactividades.recordActivity.presentation

import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aitor.trackactividades.recordActivity.presentation.model.ScreenTypes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordActivityScreen(
    recordActivityViewModel: RecordActivityViewModel,
    navigateToFeed: () -> Unit
) {
    val context = LocalContext.current
    val screenMode by recordActivityViewModel.screenMode.observeAsState(initial = ScreenTypes.START_ACTIVITY)
    val userLocation by recordActivityViewModel.filteredUserLocation.observeAsState()
    val routeCoordinates by recordActivityViewModel.routeCoordinates.observeAsState(initial = emptyList())
    val stopwatch by recordActivityViewModel.stopwatch.observeAsState(initial = 0L)
    val distance by recordActivityViewModel.distance.observeAsState(initial = 0f)
    val speed by recordActivityViewModel.speed.observeAsState(initial = 0f)
    val altitude by recordActivityViewModel.altitude.observeAsState(initial = 0.0)
    val slope by recordActivityViewModel.slope.observeAsState(initial = 0f)

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
            BottomAppBar() {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    when (screenMode) {
                        ScreenTypes.START_ACTIVITY -> {
                            Button(
                                onClick = {
                                    recordActivityViewModel.start(context)
                                },
                                modifier = Modifier
                                    .size(120.dp), // Botón más grande
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text("Iniciar")
                            }
                        }

                        ScreenTypes.RECORD_ACTIVITY, ScreenTypes.MAP_RECORD_ACTIVITY -> {
                            // Botón para detener la actividad
                            Button(
                                onClick = {
                                    recordActivityViewModel.pause()
                                },
                                modifier = Modifier.size(120.dp), // Botón grande
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop")
                            }

                            // Botón para alternar entre mapa y estadísticas (más pequeño)
                            val isMapMode = screenMode == ScreenTypes.MAP_RECORD_ACTIVITY
                            Button(
                                onClick = {
                                    val newMode = if (isMapMode) ScreenTypes.RECORD_ACTIVITY else ScreenTypes.MAP_RECORD_ACTIVITY
                                    recordActivityViewModel.setScreenMode(newMode)
                                },
                                modifier = Modifier.size(90.dp), // Botón más pequeño
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isMapMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                                    contentColor = if (isMapMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = "Map",
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        ScreenTypes.PAUSE_ACTIVITY -> {
                            Button(
                                onClick = {
                                    recordActivityViewModel.resume()
                                },
                                modifier = Modifier.size(120.dp), // Botón grande
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text("Reanudar")
                            }
                            Button(
                                onClick = { /* Guardar actividad */ },
                                modifier = Modifier.size(120.dp), // Botón grande
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (screenMode) {
                ScreenTypes.START_ACTIVITY -> StartActivityContent(
                    userLocation,
                    routeCoordinates
                )

                ScreenTypes.RECORD_ACTIVITY -> StatsMode(
                    stopwatch = stopwatch,
                    speed = speed,
                    distance = distance,
                    altitude = altitude,
                    slope = slope,
                    recordActivityViewModel = recordActivityViewModel
                )

                ScreenTypes.MAP_RECORD_ACTIVITY -> GoogleMapView(userLocation, routeCoordinates)
                ScreenTypes.PAUSE_ACTIVITY -> PauseMode(userLocation, routeCoordinates)
            }
        }
    }
}

@Composable
fun PauseMode(userLocation: Location?, routeCoordinates: List<LatLng>) {
    GoogleMapView(userLocation, routeCoordinates)
}


@Composable
fun StartActivityContent(
    userLocation: Location?,
    routeCoordinates: List<LatLng>
) {
    GoogleMapView(userLocation, routeCoordinates)
}

@OptIn(FlowPreview::class)
@Composable
fun GoogleMapView(userLocation: Location?, routeCoordinates: List<LatLng>) {
    val cameraPositionState = rememberCameraPositionState()
    var isFollowingUser by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // Evitar múltiples ejecuciones rápidas
    val debounceScope = rememberCoroutineScope()

    LaunchedEffect(userLocation) {
        Log.d("Maps", "Se ejecuta el LaunchedEffect userLocation")
        userLocation?.let {
            if (isFollowingUser) {
                Log.d("Maps", "Se modifica la posicion de la camara")
                coroutineScope.launch {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(
                            LatLng(it.latitude, it.longitude), 15f
                        ),
                        durationMs = 1000
                    )
                }
            }
        }
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = true),
        uiSettings = MapUiSettings(myLocationButtonEnabled = true),
        onMyLocationButtonClick = {
            isFollowingUser = !isFollowingUser // Se vuelve a seguir al usuario
            Log.d("Maps", "Se vuelve a seguir al usuario")

            // Espera unos milisegundos para evitar que snapshotFlow desactive el seguimiento de inmediato
            debounceScope.launch {
                delay(500) // Espera 500ms antes de actualizar la cámara
                userLocation?.let {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngZoom(
                            LatLng(it.latitude, it.longitude), 15f
                        ),
                        durationMs = 1000
                    )
                }
            }
            false // Permite que Google Maps haga la animación de centrado
        }
    ) {
        if (routeCoordinates.isNotEmpty()) {
            Polyline(
                points = routeCoordinates,
                color = MaterialTheme.colorScheme.primary,
                width = 8f
            )
        }
    }
}


@Composable
fun StatsMode(
    stopwatch: Long,
    speed: Float,
    distance: Float,
    altitude: Double,
    slope: Float,
    recordActivityViewModel: RecordActivityViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatItem(title = "Tiempo", value = recordActivityViewModel.formatTime(stopwatch), modifier = Modifier.weight(1f))
        Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f), thickness = 1.dp)

        StatItem(title = "Distancia", value = "${"%.2f".format(distance / 1000)} km", modifier = Modifier.weight(1f))
        Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f), thickness = 1.dp)

        StatItem(title = "Velocidad", value = "${"%.2f".format(speed)} km/h", modifier = Modifier.weight(1f))
        Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f), thickness = 1.dp)

        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically

        ) {
            StatItem(title = "Altitud", value = String.format(Locale.FRANCE, "%.2f", altitude), modifier = Modifier.weight(1f))
            StatItem(title = "Pendiente", value = String.format(Locale.FRANCE, "%.2f", slope), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun StatItem(title: String, value: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            fontSize = 48.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
}
