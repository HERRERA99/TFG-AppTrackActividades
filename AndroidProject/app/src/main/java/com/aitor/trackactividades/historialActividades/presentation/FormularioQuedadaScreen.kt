package com.aitor.trackactividades.historialActividades.presentation

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
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
                FechaHoraInput(formularioQuedadaViewModel, fechaHora)
                LocalizacionInput(formularioQuedadaViewModel, localizacion)
                ParticipantesInput(formularioQuedadaViewModel, maxParticipantes)
                ModalidadDropdown(formularioQuedadaViewModel, modalidad)
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
    Button(
        onClick = {
            viewModel.guardarQuedada()
            navigateToQuedadas()
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding()
    ) {
        Text("Crear quedada")
    }
}

@Composable
fun ModalidadDropdown(viewModel: FormularioQuedadaViewModel, modalidad: Modalidades) {
    var expanded by remember { mutableStateOf(false) }

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


@Composable
fun ParticipantesInput(viewModel: FormularioQuedadaViewModel, maxParticipantes: String) {
    OutlinedTextField(
        value = maxParticipantes,
        onValueChange = { viewModel.actualizarMaxParticipantes(it) },
        label = { Text("Máximo de participantes (vacío = sin límite)") },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun LocalizacionInput(viewModel: FormularioQuedadaViewModel, localizacion: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Mostramos la ubicación seleccionada
        if (localizacion.isNotEmpty()) {
            Text(
                text = "Ubicación seleccionada:",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = localizacion,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // Botón para seleccionar ubicación
        Button(
            onClick = { viewModel.toggleMostrarMapa(true) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Seleccionar lugar de quedada")
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

    OutlinedTextField(
        value = fechaHora.toString(),
        onValueChange = {},
        label = { Text("Día y Hora") },
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
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
        readOnly = true,
        singleLine = true
    )
}


@Composable
fun DescripcionInput(viewModel: FormularioQuedadaViewModel, descripcion: String) {
    OutlinedTextField(
        value = descripcion,
        onValueChange = { viewModel.actualizarDescripcion(it) },
        label = { Text("Descripción") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun TituloInput(viewModel: FormularioQuedadaViewModel, titulo: String) {
    OutlinedTextField(
        value = titulo,
        onValueChange = { viewModel.actualizarTitulo(it) },
        label = { Text("Título") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}