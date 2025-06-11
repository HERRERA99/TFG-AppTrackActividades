package com.aitor.trackactividades.quedadas.presentation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
                                onClick = {},
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
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun QuedadaBody(meetup: Meetup, detallesQuedadaViewModel: DetallesQuedadaViewModel, onUserClick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        DetallesQuedadaBody(meetup, meetup.route.isNotEmpty(), detallesQuedadaViewModel)
        Spacer(modifier = Modifier.height(16.dp))
        if (meetup.route.isNotEmpty()) {
            RutaQuedada(meetup)
        }
        ParticipantesQuedada(meetup) {
            onUserClick(it)
        }
    }
}

@Composable
fun RutaQuedada(meetup: Meetup) {
    val context = LocalContext.current
    val color = MaterialTheme.colorScheme.primary
    val primaryColorHex = remember(color) {
        String.format("0x%06X", (0xFFFFFF and color.toArgb())).replace("0x", "")
    }

    val route = meetup.route
    val mapUrl = remember(route, primaryColorHex) {
        if (route.isEmpty()) "" else {
            val path = route.joinToString("|") { "${it.latitude},${it.longitude}" }
            "https://maps.googleapis.com/maps/api/staticmap?" +
                    "size=600x300&" +
                    "path=color:0x$primaryColorHex|weight:5|$path&" +
                    "markers=color:red|${route.first().latitude},${route.first().longitude}&" +
                    "markers=color:green|${route.last().latitude},${route.last().longitude}&" +
                    "key=AIzaSyAmTzpoTJ8HnqJCNAhR2UID4IAX94lqvKY"
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth().background(Color.Red),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (mapUrl.isNotEmpty()) {
            AsyncImage(
                model = mapUrl,
                contentDescription = "Mapa de la ruta",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.FillWidth
            )
        }
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
        Text(
            text = meetup.description,
            style = MaterialTheme.typography.bodyLarge
        )

        // Fila para la modalidad (icono + nombre)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
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
        }

        // Hora
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    maxLines = Int.MAX_VALUE, // para que haga wrap en todas las líneas necesarias
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


        // Altitud
        if (tieneRuta) {
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
            }
        }

        // Distancia
        if (tieneRuta) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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
            }
        }
    }
}

@Composable
fun ParticipantesQuedada(meetup: Meetup, onUserClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Participantes",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "${meetup.participants.size}/${meetup.maxParticipants ?: "∞"}",
            style = MaterialTheme.typography.bodyMedium
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
