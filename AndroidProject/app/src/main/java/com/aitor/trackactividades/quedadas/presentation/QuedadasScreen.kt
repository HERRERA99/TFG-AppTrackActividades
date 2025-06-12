package com.aitor.trackactividades.quedadas.presentation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.aitor.trackactividades.feed.presentation.FeedBottomBar
import com.aitor.trackactividades.quedadas.presentation.model.ItemMeetupList
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun QuedadasScreen(
    quedadasViewModel: QuedadasViewModel,
    navigateToStartRecordActivity: () -> Unit,
    navigateToFeed: () -> Unit,
    navigateToHistorial: () -> Unit,
    navigateToFormularioQuedada: () -> Unit,
    navigateToDetallesQuedada: (Long) -> Unit
) {
    val context = LocalContext.current
    val meetups = quedadasViewModel.meetups.collectAsLazyPagingItems()
    val locationState by quedadasViewModel.location.collectAsState()

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

    // Efecto para refrescar cuando se obtiene la ubicación
    LaunchedEffect(locationState) {
        if (locationState != null) {
            quedadasViewModel.refresh()
        }
    }

    // Manejo de estados de carga
    when {
        meetups.loadState.refresh is LoadState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        meetups.loadState.refresh is LoadState.Error -> {
            val error = meetups.loadState.refresh as LoadState.Error
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Error al cargar las quedadas")
                    Text(error.error.localizedMessage ?: "Error desconocido")
                    Button(onClick = { meetups.retry() }) {
                        Text("Reintentar")
                    }
                }
            }
        }

        locationState == null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Obteniendo ubicación...")
                    CircularProgressIndicator()
                }
            }
        }

        else -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Quedadas",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )
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
                        onHistorialClick = navigateToHistorial,
                        onFeedClick = { navigateToFeed() },
                        onQuedadasClick = null
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { navigateToFormularioQuedada() },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Crear quedada")
                    }
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    QuedadasTapScreen(
                        quedadasViewModel = quedadasViewModel,
                        meetups = meetups,
                        navigateToDetallesQuedada = navigateToDetallesQuedada
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuedadasTapScreen(
    quedadasViewModel: QuedadasViewModel,
    meetups: LazyPagingItems<ItemMeetupList>,
    navigateToDetallesQuedada: (Long) -> Unit
) {
    var tabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Quedadas", "Mis Quedadas")
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { tabs.size }
    )

    // Sync the pager state with the tab index
    LaunchedEffect(tabIndex) {
        pagerState.animateScrollToPage(tabIndex)
    }

    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
        if (!pagerState.isScrollInProgress) {
            tabIndex = pagerState.currentPage
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = tabIndex,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier
                        .height(2.dp)
                        .tabIndicatorOffset(tabPositions[tabIndex]),
                    color = MaterialTheme.colorScheme.primary
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = tabIndex == index,
                    onClick = { tabIndex = index },
                    icon = {
                        when (index) {
                            0 -> Icon(imageVector = Icons.Default.Home, contentDescription = null)
                            1 -> Icon(imageVector = Icons.Default.List, contentDescription = null)
                        }
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> QuedadasBody(
                    viewModel = quedadasViewModel,
                    meetups = meetups,
                    navigateToDetallesQuedada = navigateToDetallesQuedada
                )

                1 -> MisQuedadasBody()
            }
        }
    }
}

@Composable
fun MisQuedadasBody() {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Mis Quedadas", fontSize = 25.sp)
    }
}

@Composable
fun QuedadasBody(
    viewModel: QuedadasViewModel,
    meetups: LazyPagingItems<ItemMeetupList>,
    navigateToDetallesQuedada: (Long) -> Unit
) {
    val isRefreshing = meetups.loadState.refresh is LoadState.Loading

    SwipeRefresh(
        state = remember { SwipeRefreshState(isRefreshing) },
        onRefresh = { meetups.refresh() }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(count = meetups.itemCount) { index ->
                meetups[index]?.let { meetup ->
                    MeetupItem(
                        meetup = meetup,
                        viewModel = viewModel,
                        navigateToDetallesQuedada = navigateToDetallesQuedada
                    )
                }
            }

            if (meetups.loadState.append is LoadState.Loading) {
                item {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun MeetupItem(
    meetup: ItemMeetupList,
    viewModel: QuedadasViewModel,
    navigateToDetallesQuedada: (Long) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { navigateToDetallesQuedada(meetup.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Título
                Text(
                    text = meetup.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                // Modalidad
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = meetup.sportType.icon,
                        contentDescription = "Modalidad deportiva",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = meetup.sportType.displayName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // Fecha
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Fecha",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = meetup.dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // Hora
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Hora",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = meetup.dateTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                // Ubicación
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Ubicación",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = viewModel.extraerPuebloProvinciaPaisSinNumeros(meetup.location),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                // Estado de participación
                Text(
                    text = if (meetup.isParticipating) "Te has apuntado" else "No estás apuntado",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (meetup.isParticipating) Color(0xFF388E3C) else Color.Gray
                )
            }
        }
    }
}

