package com.aitor.trackactividades.recordActivity.presentation

import android.location.Location
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.aitor.trackactividades.core.model.Modalidades
import com.aitor.trackactividades.recordActivity.presentation.model.ScreenTypes
import com.aitor.trackactividades.recordActivity.presentation.utils.FormatTime.formatTime
import com.aitor.trackactividades.recordActivity.presentation.utils.SpeedManager.speedConversor
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordActivityScreen(
    recordActivityViewModel: RecordActivityViewModel,
    navigateToFeed: () -> Unit
) {
    val context = LocalContext.current
    val screenMode by recordActivityViewModel.screenMode.observeAsState(initial = ScreenTypes.START_ACTIVITY)
    val userLocation by recordActivityViewModel.userLocation.observeAsState()
    val routeCoordinates by recordActivityViewModel.routeCoordinates.observeAsState(initial = emptyList())
    val stopwatch by recordActivityViewModel.stopwatch.observeAsState(initial = 0L)
    val distance by recordActivityViewModel.distance.observeAsState(initial = 0f)
    val speed by recordActivityViewModel.speed.observeAsState(initial = 0f)
    val calories by recordActivityViewModel.calories.observeAsState(initial = 0f)
    val activityType by recordActivityViewModel.activityType.observeAsState(initial = Modalidades.CICLISMO_CARRETERA)
    val activityTitle by recordActivityViewModel.activityTitle.observeAsState(initial = "")

    // Estado para controlar la visibilidad del AlertDialog de guardar
    var showSaveDialog by remember { mutableStateOf(false) }
    // Estado para controlar la visibilidad del AlertDialog de descartar
    var showDiscardDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = {
                        // Mostrar diálogo de confirmación al pulsar cerrar
                        showDiscardDialog = true
                    }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            ) {
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
                                    .size(120.dp),
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
                            Button(
                                onClick = {
                                    recordActivityViewModel.pause()
                                },
                                modifier = Modifier.size(120.dp),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop")
                            }

                            val isMapMode = screenMode == ScreenTypes.MAP_RECORD_ACTIVITY
                            Button(
                                onClick = {
                                    val newMode =
                                        if (isMapMode) ScreenTypes.RECORD_ACTIVITY else ScreenTypes.MAP_RECORD_ACTIVITY
                                    recordActivityViewModel.setScreenMode(newMode)
                                },
                                modifier = Modifier.size(90.dp),
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
                                modifier = Modifier.size(120.dp),
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text("Reanudar")
                            }
                            Button(
                                onClick = {
                                    showSaveDialog = true // Mostrar el diálogo de guardar
                                },
                                modifier = Modifier.size(120.dp),
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
                    userLocation = userLocation,
                    activityType = activityType,
                    routeCoordinates = routeCoordinates,
                    recordActivityViewModel = recordActivityViewModel
                )

                ScreenTypes.RECORD_ACTIVITY -> StatsMode(
                    stopwatch = stopwatch,
                    speed = speed,
                    distance = distance,
                    calories = calories,
                    recordActivityViewModel = recordActivityViewModel
                )

                ScreenTypes.MAP_RECORD_ACTIVITY -> GoogleMapView(
                    userLocation,
                    Modifier,
                    routeCoordinates
                )

                ScreenTypes.PAUSE_ACTIVITY -> PauseMode(
                    userLocation,
                    routeCoordinates,
                    stopwatch,
                    distance,
                    calories
                )
            }

            // Diálogo para guardar la actividad
            SaveActivityDialog(
                showSaveDialog = showSaveDialog,
                onDismissRequest = { showSaveDialog = false },
                onSave = { newTitle, isPublic ->
                    recordActivityViewModel.setVisibility(isPublic)
                    recordActivityViewModel.save()
                    navigateToFeed()
                },
                onTitleChange = {
                    recordActivityViewModel.setActivityTitle(it)
                },
                recordActivityViewModel = recordActivityViewModel,
                initialPublicState = true,
                title = activityTitle,
            )

            // Diálogo para descartar la actividad
            DiscardActivityDialog(
                showDiscardDialog = showDiscardDialog,
                onDismissRequest = { showDiscardDialog = false },
                onConfirmDiscard = {
                    // Descarta la actividad y navega al feed
                    recordActivityViewModel.discard()
                    navigateToFeed()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveActivityDialog(
    showSaveDialog: Boolean,
    onDismissRequest: () -> Unit,
    onSave: (String, Boolean) -> Unit, // Ahora recibe título y visibilidad
    onTitleChange: (String) -> Unit,
    recordActivityViewModel: RecordActivityViewModel,
    initialPublicState: Boolean = true,
    title: String,
) {
    var isPublic by remember { mutableStateOf(initialPublicState) }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            modifier = Modifier.fillMaxWidth(0.9f),
            properties = DialogProperties(dismissOnClickOutside = true),
            content = {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Guardar Actividad",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Campo de texto para el título
                        Text(
                            text = "Título de la actividad:",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = title,
                            onValueChange = { onTitleChange(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            label = { Text("Título") },
                            placeholder = { Text(recordActivityViewModel.nombreAutomatico(
                                OffsetDateTime.now(), recordActivityViewModel.activityType.value!!)) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            )
                        )

                        // Selector de visibilidad con Switch
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Switch(
                                checked = isPublic,
                                onCheckedChange = { isPublic = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                                    checkedTrackColor = MaterialTheme.colorScheme.secondary
                                )
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Actividad pública",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = if (isPublic) "Visible para otros usuarios"
                                    else "Solo visible para ti",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }

                        // Botones de acción
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(
                                onClick = onDismissRequest,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text("Cancelar")
                            }
                            TextButton(
                                onClick = {
                                    onSave(title, isPublic)
                                },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text("Guardar", fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun PauseMode(
    userLocation: Location?,
    routeCoordinates: List<LatLng>,
    stopwatch: Long,
    distance: Float,
    calories: Float
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Mapa
        GoogleMapView(userLocation, Modifier.weight(1f), routeCoordinates)

        // Sección de estadísticas
        StatsSection(
            stopwatch = stopwatch,
            distance = distance,
            calories = calories,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}

@Composable
fun StatsSection(
    stopwatch: Long,
    distance: Float,
    calories: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatItem(
            title = "Tiempo",
            value = formatTime(stopwatch),
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Divider(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        StatItem(
            title = "Distancia",
            value = "${"%.2f".format(distance / 1000)} km",
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Divider(
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
            thickness = 1.dp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        StatItem(
            title = "Calorías",
            value = String.format(Locale.FRANCE, "%.0f", calories),
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
fun StartActivityContent(
    userLocation: Location?,
    activityType: Modalidades,
    routeCoordinates: List<LatLng>,
    recordActivityViewModel: RecordActivityViewModel
) {
    Column(modifier = Modifier.fillMaxSize()) {
        GoogleMapView(userLocation, Modifier.weight(1f), routeCoordinates)
        ActivityTypeInput(activityType, Modifier) {
            recordActivityViewModel.onTypeChange(it)
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
fun GoogleMapView(userLocation: Location?, modifier: Modifier, routeCoordinates: List<LatLng>) {
    val cameraPositionState = rememberCameraPositionState()
    var isFollowingUser by remember { mutableStateOf(true) }
    val coroutineScope = rememberCoroutineScope()

    // Evitar múltiples ejecuciones rápidas
    val debounceScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (userLocation != null) {
            Log.d("Maps", "Se ejecuta el LaunchedEffect con localizacion")
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(userLocation.latitude, userLocation.longitude),
                    15f // Nivel de zoom adecuado (15f es un buen valor para calles)
                )
            )
        } else {
            // Posición por defecto si no hay ubicación (opcional)
            Log.d("Maps", "Se ejecuta el LaunchedEffect sin localizacion")
            cameraPositionState.move(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(0.0, 0.0),
                    2f // Zoom muy alejado
                )
            )
        }
    }

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

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.matchParentSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false
            ),
            onMyLocationButtonClick = {
                isFollowingUser = !isFollowingUser // Se vuelve a seguir al usuario
                Log.d("Maps", "isFollowingUser = $isFollowingUser")

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

        FloatingActionButton(
            onClick = { isFollowingUser = !isFollowingUser },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = if (isFollowingUser) Icons.Default.MyLocation else Icons.Default.LocationSearching,
                contentDescription = "Toggle Seguimiento",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}


@Composable
fun StatsMode(
    stopwatch: Long,
    speed: Float,
    distance: Float,
    calories: Float,
    recordActivityViewModel: RecordActivityViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StatItem(title = "Tiempo", value = formatTime(stopwatch), modifier = Modifier.weight(1f))
        Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f), thickness = 1.dp)

        StatItem(
            title = "Distancia",
            value = "${"%.2f".format(distance / 1000)} km",
            modifier = Modifier.weight(1f)
        )
        Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f), thickness = 1.dp)

        StatItem(
            title = "Velocidad",
            value = speedConversor(speed, recordActivityViewModel.activityType.value!!),
            modifier = Modifier.weight(1f)
        )
        Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f), thickness = 1.dp)

        StatItem(
            title = "Calorias",
            value = String.format(Locale.FRANCE, "%.0f", calories),
            modifier = Modifier.weight(1f)
        )
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
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ActivityTypeInput(
    selectedType: Modalidades,
    modifier: Modifier,
    onTypeSelected: (Modalidades) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val typeOptions = Modalidades.entries.toTypedArray()
    var type by remember { mutableStateOf(selectedType) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = type.displayName,
            onValueChange = {},
            modifier = modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            colors = TextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onBackground,
                disabledContainerColor = Color.Transparent,
                disabledIndicatorColor = MaterialTheme.colorScheme.primary,
                disabledLabelColor = MaterialTheme.colorScheme.onPrimary,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            ),
            leadingIcon = {
                Icon(
                    imageVector = type.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            readOnly = true,
            enabled = false,
            label = { Text(text = "Modalidad", color = MaterialTheme.colorScheme.primary) }
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            typeOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.displayName) },
                    onClick = {
                        type = option
                        onTypeSelected(option)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = option.icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = MaterialTheme.colorScheme.background,
                        leadingIconColor = MaterialTheme.colorScheme.primary,
                        trailingIconColor = MaterialTheme.colorScheme.onBackground,
                        disabledTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f),
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f),
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f)
                    )

                )
            }
        }
    }
}

@Composable
fun DiscardActivityDialog(
    showDiscardDialog: Boolean,
    onDismissRequest: () -> Unit,
    onConfirmDiscard: (isPublic: Boolean) -> Unit, // Ahora recibe un parámetro booleano
    initialPublicState: Boolean = true // Estado inicial del RadioButton
) {
    var isPublic by remember { mutableStateOf(initialPublicState) } // Estado local para el RadioButton

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(text = "Descartar actividad")
            },
            text = {
                Text("¿Estás seguro de que quieres descartar esta actividad? Todos los datos se perderán.")
            },
            confirmButton = {
                TextButton(
                    onClick = { onConfirmDiscard(isPublic) } // Pasamos el estado de visibilidad
                ) {
                    Text("Descartar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissRequest
                ) {
                    Text("Cancelar")
                }
            },
            containerColor = MaterialTheme.colorScheme.background,
            textContentColor = MaterialTheme.colorScheme.onBackground,
        )
    }
}