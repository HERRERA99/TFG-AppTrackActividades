package com.aitor.trackactividades.quedadas.presentation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.aitor.trackactividades.buscarUsuario.presentation.UserSearchItem
import com.aitor.trackactividades.quedadas.presentation.model.Meetup
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesQuedadaScreen(
    detallesQuedadaViewModel: DetallesQuedadaViewModel,
    navigateToQuedadas: () -> Unit,
    navigateToProfile: (Int) -> Unit
) {
    val meetup by detallesQuedadaViewModel.meetup.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detalles",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    if (meetup != null) {
                        if (detallesQuedadaViewModel.isCreator()) {
                            Button(
                                onClick = { /* Acción para eliminar la quedada */ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(text = "Eliminar")
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (meetup!!.isParticipating) {
                                        // Acción para cancelar la quedada
                                    } else {
                                        detallesQuedadaViewModel.onJoinClick(meetup!!.id)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (meetup!!.isParticipating) Color.Gray else MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(text = if (meetup!!.isParticipating) "Cancelar" else "Apuntarse")
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateToQuedadas) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (meetup != null) {
                QuedadaBody(meetup = meetup!!, detallesQuedadaViewModel) { navigateToProfile(it) }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun QuedadaBody(
    meetup: Meetup,
    detallesQuedadaViewModel: DetallesQuedadaViewModel,
    onUserClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        DetallesQuedadaBody(meetup, meetup.route.isNotEmpty(), detallesQuedadaViewModel)
        Spacer(modifier = Modifier.height(16.dp))
        if (meetup.route.isNotEmpty()) {
            MiniMapaRuta(meetup)
        }
        ParticipantesQuedada(meetup) {
            onUserClick(it)
        }
    }
}

@Composable
fun MiniMapaRuta(meetup: Meetup) {
    val route = meetup.route
    val cameraPositionState = rememberCameraPositionState {
        if (route.isNotEmpty()) {
            position = CameraPosition.fromLatLngZoom(route.first(), 14f)
        }
    }

    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                // Navegar a otra pantalla de mapa completo
            },
        cameraPositionState = cameraPositionState
    ) {
        Polyline(
            points = route,
            color = Color.Red,
            width = 5f
        )
        Marker(state = rememberMarkerState(position = route.first()), title = "Inicio")
        Marker(state = rememberMarkerState(position = route.last()), title = "Fin")
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Altitud y distancia
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.TrendingUp,
            contentDescription = "Altitud",
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${"%.2f".format(meetup.elevationGain)} m",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(16.dp))
        Icon(
            imageVector = Icons.Default.Route,
            contentDescription = "Distancia",
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${"%.2f".format(meetup.distance)} km",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(16.dp))
        // Fila para la modalidad (icono + nombre)
        Icon(
            imageVector = meetup.sportType.icon,
            contentDescription = "Modalidad deportiva",
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = meetup.sportType.displayName,
            style = MaterialTheme.typography.bodyMedium
        )

    }
}

@Composable
fun DetallesQuedadaBody(meetup: Meetup, tieneRuta: Boolean, viewModel: DetallesQuedadaViewModel) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Título
        Text(
            text = meetup.title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        // Descripción
        if (meetup.description != null) {
            Text(
                text = meetup.description,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Fecha
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Fecha",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = meetup.dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = "Hora",
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = meetup.dateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Lugar + Botón "Ir"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Ubicación",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = meetup.location,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = Int.MAX_VALUE,
                    overflow = TextOverflow.Clip
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = {
                    viewModel.abrirGoogleMaps(
                        context,
                        meetup.locationCoordinates.latitude,
                        meetup.locationCoordinates.longitude
                    )
                },
                modifier = Modifier
                    .defaultMinSize(minWidth = 80.dp) // tamaño mínimo deseado
                    .height(IntrinsicSize.Min)       // se adapta a su contenido vertical
            ) {
                Icon(Icons.Default.Directions, contentDescription = "Ir")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ir")
            }
        }
    }
}

@Composable
fun ParticipantesQuedada(meetup: Meetup, onUserClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Participantes",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Participantes: ${meetup.participants.size}",
            style = MaterialTheme.typography.bodyMedium
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        meetup.participants.forEachIndexed { index, user ->
            UserSearchItem(
                user = user,
                isCreator = (user.id == meetup.organizerId),
                onClick = { onUserClick(user.id) }
            )
            if (index < meetup.participants.lastIndex) {
                Divider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    thickness = 1.dp
                )
            }
        }
    }

}
