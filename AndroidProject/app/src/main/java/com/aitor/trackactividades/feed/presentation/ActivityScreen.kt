package com.aitor.trackactividades.feed.presentation

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aitor.trackactividades.core.model.Modalidades
import com.aitor.trackactividades.feed.presentation.model.Publication
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    activityViewModel: ActivityViewModel,
    navigateToHome: () -> Unit
) {
    val publication by activityViewModel.publication.collectAsState()

    val scaffoldState = rememberBottomSheetScaffoldState()

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 120.dp,
        sheetContent = {
            ContenidoDetalladoActivity(
                publication = publication,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        },
        sheetDragHandle = { BottomSheetDefaults.DragHandle() },
        sheetContainerColor = MaterialTheme.colorScheme.background,
        sheetContentColor = MaterialTheme.colorScheme.onBackground
    ) {
        MapaActividad(publication = publication)
    }
}

@Composable
fun ContenidoDetalladoActivity(publication: Publication?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        if (publication == null) {
            Text("No se ha podido cargar la actividad")
        } else {
            CabeceraActividad(
                imagenPerfil = publication.user?.imageUrl!!,
                nombreUsuario = publication.user.username!!,
                fecha = publication.startTime!!,
                tipoActividad = publication.activityType!!
            )
            TituloActividad(titulo = publication.title!!)
            DetallesActividad(publication = publication)
            GraficoAltitud(publication = publication)
            GraficoVelocidad(publication = publication)
        }
        Log.d("Distancias", publication?.distances.toString())
    }
}

@Composable
fun GraficoVelocidad(publication: Publication, modifier: Modifier = Modifier) {
    val speeds = publication.speeds
    val distances = publication.distances

    if (speeds.isEmpty()) {
        Text(
            text = "No hay datos de elevación disponibles",
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            lineSeries {
                series(
                    x = distances,
                    y = speeds
                )
            }
        }
    }

    Text(text = "Velocidad", fontWeight = FontWeight.Bold, fontSize = 24.sp)
    Spacer(modifier = Modifier.height(16.dp))
    GraficoLineas(modelProducer, modifier)
}

@Composable
fun GraficoAltitud(publication: Publication, modifier: Modifier = Modifier) {
    val elevations = publication.elevations
    val distances = publication.distances

    if (elevations.isEmpty()) {
        Text(
            text = "No hay datos de elevación disponibles",
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    val modelProducer = remember { CartesianChartModelProducer() }
    LaunchedEffect(Unit) {
        modelProducer.runTransaction {
            lineSeries {
                series(
                    x = distances,
                    y = elevations
                )
            }
        }
    }

    Text(text = "Desnivel", fontWeight = FontWeight.Bold, fontSize = 24.sp)
    Spacer(modifier = Modifier.height(16.dp))
    GraficoLineas(modelProducer, modifier)
}

@Composable
private fun GraficoLineas(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier
) {
    CartesianChartHost(
        chart =
        rememberCartesianChart(
            rememberLineCartesianLayer(),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(),
        ),
        modelProducer = modelProducer,
        modifier = modifier,
    )
}

@Composable
fun DetallesActividad(publication: Publication) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ElementoActividad("Distancia", publication.formatDistance(), Modifier.weight(1f))
            ElementoActividad(
                "Desnivel positivo",
                "${publication.positiveElevation} m",
                Modifier.weight(1f)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ElementoActividad(
                "Tiempo en movimiento",
                publication.formatDuration(),
                Modifier.weight(1f)
            )
            ElementoActividad(
                "Velocidad media",
                "${publication.averageSpeed} km/h",
                Modifier.weight(1f)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ElementoActividad("Calorías", "${publication.calories} kcal", Modifier.weight(1f))
            ElementoActividad("Altitud máx.", "${publication.maxAltitude} m", Modifier.weight(1f))
        }
    }
}


@Composable
fun ElementoActividad(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = valor, fontSize = 14.sp)
    }
}


@Composable
fun TituloActividad(titulo: String) {
    Text(text = titulo, fontWeight = FontWeight.Bold, fontSize = 24.sp)
}

@Composable
fun MapaActividad(publication: Publication?) {
    if (publication == null) return

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(publication.route.first(), 12f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = false),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = false,
            zoomControlsEnabled = false
        ),
    ) {
        if (publication.route.isNotEmpty()) {
            Polyline(
                points = publication.route,
                color = MaterialTheme.colorScheme.primary,
                width = 12f
            )
        }
    }
}

@Composable
fun CabeceraActividad(
    imagenPerfil: String,
    nombreUsuario: String,
    fecha: LocalDateTime,
    tipoActividad: Modalidades
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = imagenPerfil,
            contentDescription = "Imagen de perfil",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = nombreUsuario,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = tipoActividad.icon,
                    contentDescription = tipoActividad.name,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = fecha.format(
                        DateTimeFormatter.ofPattern("d MMM yyyy 'a las' HH:mm")
                    ),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp
                    )
                )
            }
        }
    }
}