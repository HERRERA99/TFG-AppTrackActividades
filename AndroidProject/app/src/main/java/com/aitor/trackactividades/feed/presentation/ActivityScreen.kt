package com.aitor.trackactividades.feed.presentation

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import com.google.maps.android.compose.rememberMarkerState
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.component.shapeComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.core.cartesian.CartesianChart
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer.LineStroke
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer.PointConnector
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.cartesian.marker.ColumnCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Fill
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.component.Component
import com.patrykandpatrick.vico.core.common.component.LineComponent
import com.patrykandpatrick.vico.core.common.component.ShapeComponent
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import com.patrykandpatrick.vico.core.common.shape.Shape
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    activityViewModel: ActivityViewModel,
    navigateToHome: () -> Unit,
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
                    .padding(16.dp),
                activityViewModel = activityViewModel
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
fun ContenidoDetalladoActivity(
    publication: Publication?,
    modifier: Modifier = Modifier,
    activityViewModel: ActivityViewModel
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
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
            GraficoVelocidad(publication = publication, activityViewModel = activityViewModel)
            GraficoAltitud(publication = publication, activityViewModel = activityViewModel)
        }
        Log.d("Distancias", publication?.distances.toString())
    }
}

@Composable
fun GraficoVelocidad(
    publication: Publication,
    modifier: Modifier = Modifier,
    activityViewModel: ActivityViewModel
) {
    val speeds = publication.speeds
    val xValues = List(speeds.size) { index -> index.toFloat() }

    Log.d("xValues", xValues.toString())

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(speeds, xValues) {
        modelProducer.runTransaction {
            lineSeries {
                series(
                    x = xValues,
                    y = speeds,
                )
            }
        }
    }

    Column(modifier = modifier) {
        Text(text = "Velocidad", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        GraficoLineas(
            modelProducer = modelProducer,
            modifier = modifier,
            valueStartAxisFormater = CartesianValueFormatter.decimal(DecimalFormat("#.##' km/h'")),
            valueMarkerFormatter = DefaultCartesianMarker.ValueFormatter.default(
                decimalFormat = DecimalFormat(
                    "#.## km/h"
                ), colorCode = false
            ),
            viewModel = activityViewModel
        )
    }

}

@Composable
fun GraficoAltitud(
    publication: Publication,
    modifier: Modifier = Modifier,
    activityViewModel: ActivityViewModel
) {
    val elevations = publication.elevations
    val xValues = List(elevations.size) { index -> index.toFloat() }

    if (elevations.isEmpty()) {
        Text(
            text = "No hay datos de elevación disponibles",
            modifier = Modifier.padding(16.dp)
        )
        return
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(elevations, xValues) {
        modelProducer.runTransaction {
            lineSeries {
                series(
                    x = xValues,
                    y = elevations
                )
            }
        }
    }
    Column(modifier = modifier) {
        Text(text = "Desnivel", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        GraficoLineas(
            modelProducer = modelProducer,
            modifier = modifier,
            valueStartAxisFormater = CartesianValueFormatter.decimal(DecimalFormat("#.##' m'")),
            valueMarkerFormatter = DefaultCartesianMarker.ValueFormatter.default(
                decimalFormat = DecimalFormat(
                    "#.## m"
                ), colorCode = false
            ),
            viewModel = activityViewModel
        )
    }
}

@Composable
private fun GraficoLineas(
    modelProducer: CartesianChartModelProducer,
    modifier: Modifier = Modifier,
    valueStartAxisFormater: CartesianValueFormatter = CartesianValueFormatter.Default,
    valueMarkerFormatter: DefaultCartesianMarker.ValueFormatter,
    viewModel: ActivityViewModel
) {
    val lineColor = MaterialTheme.colorScheme.primary

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider =
                LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(fill(lineColor)),
                        areaFill =
                        LineCartesianLayer.AreaFill.single(
                            fill(
                                ShaderProvider.verticalGradient(
                                    intArrayOf(
                                        lineColor.copy(alpha = 0.4f).toArgb(),
                                        Color.Transparent.toArgb()
                                    )
                                )
                            )
                        )
                    )
                ),
            ),
            startAxis = VerticalAxis.rememberStart(valueFormatter = valueStartAxisFormater),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = CartesianValueFormatter { context, x, vertical ->
                    viewModel.formatSeconds(context, x, vertical)
                }
            ),
            marker = rememberDefaultCartesianMarker(
                label = rememberTextComponent(
                    background = shapeComponent(
                        fill = fill(MaterialTheme.colorScheme.primary),
                        shape = viewModel.RoundedShape(24f),
                        strokeFill = fill(MaterialTheme.colorScheme.primary),
                        strokeThickness = 1.dp,
                        shadow = null
                    ),
                    typeface = Typeface.MONOSPACE,
                    textSize = 16.sp,
                    padding = Insets(4f, 12f, 4f, 12f)
                ),
                valueFormatter = valueMarkerFormatter,
                labelPosition = DefaultCartesianMarker.LabelPosition.Top,
                guideline = rememberLineComponent(
                    thickness = 1.dp,
                    fill = fill(color = MaterialTheme.colorScheme.onBackground),
                ),
            ),
        ),
        modelProducer = modelProducer,
        scrollState = rememberVicoScrollState(scrollEnabled = false),
        modifier = modifier
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
            ElementoActividad(
                "Distancia",
                publication.formatDistance(),
                Modifier.weight(1f)
            )
            ElementoActividad(
                "Desnivel positivo",
                "%.2f".format(publication.positiveElevation) + " m",
                Modifier.weight(1f)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ElementoActividad(
                "Duración",
                publication.formatDuration(),
                Modifier.weight(1f)
            )
            ElementoActividad(
                "Velocidad media",
                "%.2f".format(publication.averageSpeed) + " km/h",
                Modifier.weight(1f)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            ElementoActividad(
                "Calorías",
                "%.2f".format(publication.calories) + " kcal",
                Modifier.weight(1f)
            )
            ElementoActividad(
                "Altitud máx.",
                "%.2f".format(publication.maxAltitude) + " m", Modifier.weight(1f)
            )
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