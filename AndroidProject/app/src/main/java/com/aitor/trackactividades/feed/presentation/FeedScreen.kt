package com.aitor.trackactividades.feed.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.paging.LoadState
import androidx.paging.compose.*
import coil.compose.rememberAsyncImagePainter
import com.aitor.trackactividades.core.compose.PublicationsList
import com.aitor.trackactividades.perfil.presentation.PostInteractionViewModel

@Composable
fun FeedScreen(
    feedViewModel: FeedViewModel,
    postInteractionViewModel: PostInteractionViewModel,
    navigateToStartRecordActivity: () -> Unit,
    navigateToActivity: (Long) -> Unit,
    navigateToSearch: () -> Unit,
    navigateToProfile: (Int) -> Unit,
    navigateToHistorial: () -> Unit,
    navigateToQuedadas: () -> Unit
) {
    val context = LocalContext.current
    val publications = feedViewModel.publications.collectAsLazyPagingItems()
    val imagen by feedViewModel.imagenPerfil.observeAsState("")
    val comentario by feedViewModel.comentario.observeAsState("")
    val userId by feedViewModel.userId.observeAsState(0)

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

    // Estado para el refresh
    val isRefreshing by remember {
        derivedStateOf {
            publications.loadState.refresh is LoadState.Loading
        }
    }

    LaunchedEffect(Unit) {
        feedViewModel.cargarImagenPerfil()
    }

    Scaffold(
        topBar = {
            FeedTopBar(
                navigateToSearch = navigateToSearch
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
                onFeedClick = null,
                onQuedadasClick = navigateToQuedadas,
                onProfileClick = navigateToProfile,
                profileImageUrl = imagen,
                userId = userId!!
            )
        }
    ) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            when {
                publications.loadState.refresh is LoadState.Loading && publications.itemCount == 0 -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                publications.loadState.refresh is LoadState.Error -> {
                    // Muestra el error detallado
                    val error = publications.loadState.refresh as LoadState.Error
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
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
                            .background(Color.Red), contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Ha ocurrido un error")
                    }
                }

                else -> {
                    PublicationsList(
                        publications = publications,
                        navigateToActivity = { publicationId ->
                            navigateToActivity(publicationId)
                        },
                        viewModel = postInteractionViewModel,
                        navigateToProfile = navigateToProfile
                    )

                    if (publications.loadState.append is LoadState.Loading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(64.dp),
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedTopBar(
    navigateToSearch: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Inicio",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = {
                navigateToSearch()
            }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    modifier = Modifier.size(48.dp)
                )
            }
            IconButton(onClick = { /* TODO: Acción de notificaciones */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones",
                    modifier = Modifier.size(48.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground,
            actionIconContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
fun FeedBottomBar(
    onRegisterClick: () -> Unit,
    onHistorialClick: (() -> Unit)?,
    onFeedClick: (() -> Unit)?,
    onQuedadasClick: (() -> Unit)?,
    onProfileClick: ((Int) -> Unit)?,
    profileImageUrl: String,
    userId: Int
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Inicio",
                    modifier = Modifier.size(32.dp)
                )
            },
            label = {
                Text(
                    "Inicio",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = if (onFeedClick != null) false else true,
            onClick = { onFeedClick?.invoke() }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Event,
                    contentDescription = "Quedadas",
                    modifier = Modifier.size(32.dp)
                )
            },
            label = {
                Text(
                    "Quedadas",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = if (onQuedadasClick != null) false else true,
            onClick = { onQuedadasClick?.invoke() }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.AddCircle,
                    contentDescription = "Registrar",
                    modifier = Modifier.size(32.dp)
                )
            },
            label = {
                Text(
                    "Registrar",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = false,
            onClick = onRegisterClick
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.ViewList,
                    contentDescription = "Historial",
                    modifier = Modifier.size(32.dp)
                )
            },
            label = {
                Text(
                    "Historial",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = if (onHistorialClick != null) false else true,
            onClick = { onHistorialClick?.invoke() }
        )
        NavigationBarItem(
            icon = {
                if (profileImageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(profileImageUrl),
                        contentDescription = "Perfil",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.Gray, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, contentDescription = "Tú", modifier = Modifier.size(32.dp))
                }
            },
            label = {
                Text("Tú", fontSize = 14.sp, fontWeight = FontWeight.Medium)
            },
            selected = if (onProfileClick != null) false else true,
            onClick = { onProfileClick?.invoke(userId) }
        )
    }
}