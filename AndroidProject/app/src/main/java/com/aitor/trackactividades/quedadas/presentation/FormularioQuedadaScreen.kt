package com.aitor.trackactividades.quedadas.presentation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aitor.trackactividades.core.model.Modalidades
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import java.time.LocalDateTime
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioQuedadaScreen(
    formularioQuedadaViewModel: FormularioQuedadaViewModel,
    navigateToQuedadas: () -> Unit
) {
    val context = LocalContext.current

    val titulo by formularioQuedadaViewModel.titulo.observeAsState("")
    val descripcion by formularioQuedadaViewModel.descripcion.observeAsState("")
    val fechaHora by formularioQuedadaViewModel.fechaHora.observeAsState(null)
    val localizacion by formularioQuedadaViewModel.localizacion.observeAsState("")
    val maxParticipantes by formularioQuedadaViewModel.maxParticipantes.observeAsState("")
    val modalidad by formularioQuedadaViewModel.modalidad.observeAsState(Modalidades.CICLISMO_CARRETERA)
    val mostrarMapa by formularioQuedadaViewModel.mostrarMapa.observeAsState(false)

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(
                    text = "Crear quedada",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            })
        }
    ) { padding ->
        if (mostrarMapa) {
            MapaSeleccionUbicacion(formularioQuedadaViewModel, modifier = Modifier.padding(padding))
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TituloInput(formularioQuedadaViewModel, titulo)
                DescripcionInput(formularioQuedadaViewModel, descripcion)
                ModalidadDropdown(formularioQuedadaViewModel, modalidad)
                FechaHoraInput(formularioQuedadaViewModel, fechaHora)
                LocalizacionInput(formularioQuedadaViewModel, localizacion)
                GpxFileUpload(formularioQuedadaViewModel)
                ParticipantesInput(formularioQuedadaViewModel, maxParticipantes)
                BotonCrearQuedada(formularioQuedadaViewModel) {
                    navigateToQuedadas()
                }
            }
        }
    }

}

@Composable
fun BotonCrearQuedada(
    viewModel: FormularioQuedadaViewModel,
    navigateToQuedadas: () -> Unit
) {
    val camposCompletos by remember {
        derivedStateOf { viewModel.camposObligatoriosCompletos() }
    }

    Button(
        onClick = {
            viewModel.guardarQuedada()
            navigateToQuedadas()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .height(50.dp),
        enabled = camposCompletos,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    ) {
        Text(
            text = "CREAR QUEDADA",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ModalidadDropdown(viewModel: FormularioQuedadaViewModel, modalidad: Modalidades) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Deporte *",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = modalidad.displayName,
                onValueChange = {},
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
                readOnly = true,
                enabled = false,
                label = { Text("Deporte") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true },
                leadingIcon = {
                    Icon(
                        imageVector = modalidad.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                Modalidades.values().forEach { modalidad ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = modalidad.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(modalidad.displayName)
                            }
                        },
                        onClick = {
                            viewModel.actualizarModalidad(modalidad)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Composable
fun ParticipantesInput(viewModel: FormularioQuedadaViewModel, maxParticipantes: String) {
    var sliderEnabled by remember { mutableStateOf(false) }
    var sliderValue by remember { mutableStateOf(5f) } // Valor inicial

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Límite de participantes",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = sliderEnabled,
                onCheckedChange = {
                    sliderEnabled = it
                    if (!it) {
                        viewModel.actualizarMaxParticipantes("")
                    } else {
                        viewModel.actualizarMaxParticipantes(sliderValue.toInt().toString())
                    }
                }
            )
        }

        if (sliderEnabled) {
            Slider(
                value = sliderValue,
                onValueChange = {
                    sliderValue = it
                    viewModel.actualizarMaxParticipantes(it.toInt().toString())
                },
                valueRange = 1f..50f,
                steps = 49,
                modifier = Modifier.padding(vertical = 8.dp),
                colors = SliderDefaults.colors(
                    activeTickColor = Color.Transparent,
                    inactiveTickColor = Color.Transparent
                )
            )

            Text(
                text = "Máximo: ${sliderValue.toInt()} participantes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Text(
                text = "Sin límite de participantes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun LocalizacionInput(viewModel: FormularioQuedadaViewModel, localizacion: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Ubicación de la quedada *",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Mostramos la ubicación seleccionada o el placeholder
        if (localizacion.isNotEmpty()) {
            Card(
                onClick = { viewModel.toggleMostrarMapa(true) },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = localizacion,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            Card(
                onClick = { viewModel.toggleMostrarMapa(true) },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Seleccionar ubicación",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun MapaSeleccionUbicacion(
    viewModel: FormularioQuedadaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    // Observamos el estado del ViewModel
    val latLng by viewModel.latLng.observeAsState()
    val localizacion by viewModel.localizacion.observeAsState("")

    // Estado inicial de la cámara (centrado en Madrid por defecto)
    val cameraPositionState = remember {
        CameraPositionState(
            position = CameraPosition.fromLatLngZoom(
                latLng ?: LatLng(40.4168, -3.7038),
                12f
            )
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.weight(1f),
            cameraPositionState = cameraPositionState,
            onMapClick = { clickedLatLng ->
                // Solo actualiza la posición sin cambiar el zoom
                cameraPositionState.position = CameraPosition.fromLatLngZoom(
                    clickedLatLng,
                    cameraPositionState.position.zoom // Mantiene el zoom actual
                )
                viewModel.obtenerDireccionDesdeCoordenadas(clickedLatLng, context)
            },
            properties = MapProperties(
                isMyLocationEnabled = true
            )
        ) {
            latLng?.let { position ->
                Marker(
                    state = MarkerState(position = position),
                    title = localizacion.ifEmpty { "Ubicación seleccionada" }
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = {
                    viewModel.limpiarUbicacionSeleccionada()
                    viewModel.toggleMostrarMapa(false)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    if (latLng != null) {
                        viewModel.toggleMostrarMapa(false)
                    } else {
                        Toast.makeText(
                            context,
                            "Selecciona una ubicación en el mapa",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Confirmar")
            }
        }
    }
}

@Composable
fun FechaHoraInput(viewModel: FormularioQuedadaViewModel, fechaHora: LocalDateTime?) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Formatear la fecha y hora para mostrarla
    val fechaHoraText = remember(fechaHora) {
        if (fechaHora != null) {
            "${fechaHora.dayOfMonth}/${fechaHora.monthValue}/${fechaHora.year} - " +
                    "${String.format("%02d", fechaHora.hour)}:${
                        String.format(
                            "%02d",
                            fechaHora.minute
                        )
                    }"
        } else {
            "Seleccionar fecha y hora"
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Fecha y hora *",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Card(
            onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                val fechaHora = LocalDateTime.of(year, month + 1, day, hour, minute)
                                viewModel.actualizarFechaHora(fechaHora)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            true
                        ).show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = fechaHoraText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (fechaHora != null) {
                        MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    }
                )
            }
        }
    }
}


@Composable
fun DescripcionInput(viewModel: FormularioQuedadaViewModel, descripcion: String) {
    val maxChar = 150
    val remainingChars = maxChar - descripcion.length

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Descripción (Opcional)",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = descripcion,
            onValueChange = {
                if (it.length <= maxChar) {
                    viewModel.actualizarDescripcion(it)
                }
            },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4,
            trailingIcon = {
                Text(
                    text = "$remainingChars/$maxChar",
                    style = MaterialTheme.typography.labelSmall,
                    color = when {
                        remainingChars < 0 -> MaterialTheme.colorScheme.error
                        remainingChars < 50 -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        )

        if (remainingChars < 0) {
            Text(
                text = "Has excedido el límite de caracteres",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@Composable
fun TituloInput(viewModel: FormularioQuedadaViewModel, titulo: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Título *",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = titulo,
            onValueChange = { viewModel.actualizarTitulo(it) },
            label = { Text("Introduce el título de la quedada") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@Composable
fun GpxFileUpload(viewModel: FormularioQuedadaViewModel) {
    val context = LocalContext.current
    val gpxFile by viewModel.gpxFile.observeAsState()
    var filePickerLaunched by remember { mutableStateOf(false) }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val fileName = it.getFileName(context)
                Log.d("GPX_UPLOAD", "Archivo seleccionado - URI: $it")
                Log.d("GPX_UPLOAD", "Nombre del archivo: $fileName")

                if (fileName.endsWith(".gpx", ignoreCase = true)) {
                    Log.i("GPX_UPLOAD", "Archivo GPX válido detectado")
                    viewModel.selectGpxFile(it)
                } else {
                    Log.w("GPX_UPLOAD", "Archivo no es GPX: $fileName")
                    Toast.makeText(
                        context,
                        "Por favor selecciona un archivo .GPX",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } ?: run {
                Log.d("GPX_UPLOAD", "Selección de archivo cancelada")
            }
            filePickerLaunched = false
        }
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Ruta GPX (opcional)",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        if (gpxFile != null) {
            Card(
                onClick = { launcher.launch("*/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = gpxFile!!.getFileName(context),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar archivo",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { viewModel.selectGpxFile(null) }
                    )
                }
            }
        } else {
            Card(
                onClick = { launcher.launch("*/*") },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Seleccionar archivo GPX",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

fun Uri.getFileName(context: Context): String {
    val cursor = context.contentResolver.query(this, null, null, null, null)
    return cursor?.use {
        it.moveToFirst()
        it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
    } ?: this.path?.substringAfterLast('/') ?: "archivo.gpx"
}