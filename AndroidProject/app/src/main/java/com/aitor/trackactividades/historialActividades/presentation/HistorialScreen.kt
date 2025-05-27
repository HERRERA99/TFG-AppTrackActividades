package com.aitor.trackactividades.historialActividades.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.aitor.trackactividades.core.compose.PublicationsList
import com.aitor.trackactividades.core.model.Modalidades
import com.aitor.trackactividades.feed.presentation.FeedBottomBar
import com.aitor.trackactividades.feed.presentation.model.Publication
import com.aitor.trackactividades.historialActividades.presentation.HistorialViewModel.FilterState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen(
    historialViewModel: HistorialViewModel,
    navigateToStartRecordActivity: () -> Unit,
    navigateToActivity: (Long) -> Unit,
    navigateToFeed: () -> Unit
) {
    val context = LocalContext.current
    val filtered by historialViewModel.isFiltered.collectAsState()
    var showFilterDialog by remember { mutableStateOf(false) }
    val publications = historialViewModel.filteredPublications.collectAsLazyPagingItems()

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
                    if (filtered) {
                        IconButton(
                            onClick = {
                                historialViewModel.resetFilter()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Filtrar",
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            showFilterDialog = true
                        }
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
        if (showFilterDialog) {
            FilterDialog(
                viewModel = historialViewModel,
                onDismiss = { showFilterDialog = false },
                onApplyFilters = { title, modalidad, minDistance, maxDistance, minElevation, maxElevation, minDuration, maxDuration, minSpeed, maxSpeed ->
                    historialViewModel.updateFilter(
                        nombre = title,
                        activityType = modalidad,
                        distanciaMin = minDistance,
                        distanciaMax = maxDistance,
                        positiveElevationMin = minElevation,
                        positiveElevationMax = maxElevation,
                        durationMin = minDuration,
                        durationMax = maxDuration,
                        averageSpeedMin = minSpeed,
                        averageSpeedMax = maxSpeed
                    )
                }
            )
        }
        Body(
            publications = publications,
            modifier = Modifier.padding(innerPadding),
            navigateToActivity = { publicationId ->
                navigateToActivity(publicationId)
            },
            navigateToProfile = {}
        )
    }
}

@Composable
fun Body(
    publications: LazyPagingItems<Publication>,
    modifier: Modifier,
    navigateToActivity: (Long) -> Unit,
    navigateToProfile: (Int) -> Unit
) {
    Box(modifier = modifier) {
        when {
            publications.loadState.refresh is LoadState.Loading && publications.itemCount == 0 -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            publications.loadState.refresh is LoadState.Error -> {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val error = publications.loadState.refresh as LoadState.Error
                    Text("Error al cargar publicaciones", color = Color.Red)
                    Text(error.error.localizedMessage ?: "Error desconocido")
                    Button(
                        onClick = { publications.retry() },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Reintentar")
                    }
                }
            }

            publications.loadState.hasError -> {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Red),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Ha ocurrido un error")
                }
            }

            else -> {
                Column(Modifier.fillMaxSize()) {  // Column para organizar lista e indicador
                    PublicationsList(
                        publications = publications,
                        navigateToActivity = navigateToActivity,
                        viewModel = null,
                        navigateToProfile = navigateToProfile,
                        modifier = Modifier.weight(1f)  // Peso para ocupar espacio disponible
                    )

                    if (publications.loadState.append is LoadState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(64.dp)
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    viewModel: HistorialViewModel,
    onDismiss: () -> Unit,
    onApplyFilters: (
        title: String,
        modalidad: Modalidades?,
        minDistance: Float,
        maxDistance: Float,
        minElevation: Double,
        maxElevation: Double,
        minDuration: Long,
        maxDuration: Long,
        minSpeed: Float,
        maxSpeed: Float
    ) -> Unit
) {
    // Estados
    var expanded by remember { mutableStateOf(false) }

    val currentFilter by viewModel.filterState.collectAsState()


    var title by remember { mutableStateOf(currentFilter.title) }
    var selectedModalidad by remember { mutableStateOf(currentFilter.modalidad) }
    var distanceRange by remember { mutableStateOf(currentFilter.distanceRange) }
    var elevationRange by remember { mutableStateOf(currentFilter.elevationRange) }
    var durationRange by remember { mutableStateOf(currentFilter.durationRange) }
    var speedRange by remember { mutableStateOf(currentFilter.speedRange) }

    // Añadimos null para opción "Todas las modalidades"
    val modalidadesList = listOf<Modalidades?>(null) + Modalidades.entries

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtrar", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título") },
                    singleLine = true,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedModalidad?.displayName ?: "Todas las modalidades",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Modalidad") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        singleLine = true
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        modalidadesList.forEach { modalidad ->
                            DropdownMenuItem(
                                text = { Text(modalidad?.displayName ?: "Todas las modalidades") },
                                onClick = {
                                    selectedModalidad = modalidad
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                val distanceEnd =
                    if (distanceRange.endInclusive >= 80f) "< 80" else "%.1f".format(distanceRange.endInclusive)
                Text("Distancia: ${"%.1f".format(distanceRange.start)} - $distanceEnd km")
                Spacer(modifier = Modifier.height(8.dp))
                RangeSlider(
                    value = distanceRange,
                    onValueChange = { distanceRange = it },
                    valueRange = 0f..80f,
                    steps = 15,
                    colors = SliderDefaults.colors(
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))
                val elevationEnd =
                    if (elevationRange.endInclusive >= 600f) "< 600" else elevationRange.endInclusive.toInt()
                        .toString()
                Text("Elevación: ${elevationRange.start.toInt()} - $elevationEnd m")
                Spacer(modifier = Modifier.height(8.dp))
                RangeSlider(
                    value = elevationRange,
                    onValueChange = { elevationRange = it },
                    valueRange = 0f..600f,
                    steps = 11,
                    colors = SliderDefaults.colors(
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Duración: ${
                        viewModel.formatDurationRange(
                            durationRange.start,
                            durationRange.endInclusive
                        )
                    } h"
                )
                Spacer(modifier = Modifier.height(8.dp))
                RangeSlider(
                    value = durationRange,
                    onValueChange = { durationRange = it },
                    valueRange = 0f..21600000f,
                    steps = 23,
                    colors = SliderDefaults.colors(
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))
                val speedEnd =
                    if (speedRange.endInclusive >= 50f) "< 50" else "%.1f".format(speedRange.endInclusive)
                Text("Velocidad: ${"%.1f".format(speedRange.start)} - $speedEnd km/h")
                Spacer(modifier = Modifier.height(8.dp))
                RangeSlider(
                    value = speedRange,
                    onValueChange = { speedRange = it },
                    valueRange = 0f..50f,
                    steps = 49,
                    colors = SliderDefaults.colors(
                        activeTickColor = Color.Transparent,
                        inactiveTickColor = Color.Transparent
                    )
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onApplyFilters(
                    title,
                    selectedModalidad,
                    distanceRange.start,
                    distanceRange.endInclusive,
                    elevationRange.start.toDouble(),
                    elevationRange.endInclusive.toDouble(),
                    durationRange.start.toLong(),
                    durationRange.endInclusive.toLong(),
                    speedRange.start,
                    speedRange.endInclusive
                )

                viewModel.updateLocalFilterState(
                    FilterState(
                        title = title,
                        modalidad = selectedModalidad,
                        distanceRange = distanceRange,
                        elevationRange = elevationRange,
                        durationRange = durationRange,
                        speedRange = speedRange
                    )
                )

                onDismiss()
            }) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}






