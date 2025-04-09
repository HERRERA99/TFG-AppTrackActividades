package com.aitor.trackactividades.feed.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import kotlinx.coroutines.launch
import com.aitor.trackactividades.feed.presentation.model.Publication
import java.time.format.DateTimeFormatter
import androidx.paging.LoadState
import androidx.paging.compose.*
import com.aitor.trackactividades.feed.presentation.model.Comment
import kotlin.random.Random

@Composable
fun FeedScreen(
    feedViewModel: FeedViewModel,
    navigateToStartRecordActivity: () -> Unit,
    navigateToActivity: (Long) -> Unit,
    navigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val publications = feedViewModel.publications.collectAsLazyPagingItems()
    val imagen by feedViewModel.imagenPerfil.observeAsState("")
    val comentario by feedViewModel.comentario.observeAsState("")

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
            FeedTopBar(
                navigateToHome = navigateToHome,
                feedViewModel = feedViewModel,
                imagenPerfil = imagen
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
                }
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
                        comentario = comentario,
                        feedViewModel = feedViewModel,
                        navigateToActivity = { publicationId ->
                            navigateToActivity(publicationId)
                        }
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

@Composable
fun PublicationsList(
    publications: LazyPagingItems<Publication>,
    navigateToActivity: (Long) -> Unit,
    comentario: String,
    feedViewModel: FeedViewModel
) {
    LazyColumn {
        items(publications.itemCount) {
            publications[it]?.let { publication ->
                PublicacionItem(
                    comentario = comentario,
                    publication = publication,
                    feedViewModel = feedViewModel,
                    navigateToActivity = {
                        publication.id?.let { id -> navigateToActivity(id) }
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedTopBar(
    navigateToHome: () -> Unit,
    feedViewModel: FeedViewModel,
    imagenPerfil: String
) {
    val coroutineScope = rememberCoroutineScope()

    TopAppBar(
        title = {
            Text(
                text = "Inicio",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = { /* TODO: Acción de notificaciones */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones",
                    modifier = Modifier.size(48.dp)
                )
            }
            IconButton(onClick = {
                coroutineScope.launch {
                    feedViewModel.logout()
                    navigateToHome()
                }
            }) {
                AsyncImage(
                    model = if (imagenPerfil.isNotEmpty()) imagenPerfil else "https://i.postimg.cc/RFSkJZtg/462076-1g-CSN462076-MG3928385-1248x702.webp",
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
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
fun FeedBottomBar(onRegisterClick: () -> Unit) {
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
            selected = false,
            onClick = { /*TODO*/ }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Map,
                    contentDescription = "Mapa",
                    modifier = Modifier.size(32.dp)
                )
            },
            label = {
                Text(
                    "Mapa",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = false,
            onClick = { /*TODO*/ }
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
                    Icons.Default.Group,
                    contentDescription = "Grupos",
                    modifier = Modifier.size(32.dp)
                )
            },
            label = {
                Text(
                    "Grupos",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = false,
            onClick = { /*TODO*/ }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Tú",
                    modifier = Modifier.size(32.dp)
                )
            },
            label = {
                Text(
                    "Tú",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            },
            selected = false,
            onClick = { /*TODO*/ }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicacionItem(
    comentario: String,
    publication: Publication,
    navigateToActivity : (Long) -> Unit,
    feedViewModel: FeedViewModel
) {
    // Estado para el Bottom Sheet
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showComments by remember { mutableStateOf(false) }

    // Usar remember con el estado del ViewModel
    val isLiked by remember(publication.id) {
        derivedStateOf {
            feedViewModel.isPublicationLiked(publication.likes, publication.id!!)
        }
    }

    // Contador de likes basado en el estado real
    val likeCount by remember(publication.likes, isLiked) {
        derivedStateOf {
            val baseCount = publication.likes.size
            if (isLiked && !publication.likes.contains(feedViewModel.userId.value)) {
                baseCount + 1
            } else if (!isLiked && publication.likes.contains(feedViewModel.userId.value)) {
                baseCount - 1
            } else {
                baseCount
            }
        }
    }

    // Observar los comentarios y el estado de carga
    val comments by feedViewModel.comments.collectAsState()
    val isLoadingComments by feedViewModel.isLoadingComments.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        onClick = {
            publication.id?.let { id ->
                navigateToActivity(id)
            }
        }
    ) {
        val color = MaterialTheme.colorScheme.primary
        val primaryColorHex = remember {
            String.format(
                "0x%06X",
                (0xFFFFFF and color.toArgb())
            ).replace("0x", "")
        }

        val route = publication.route
        val mapUrl = remember(route, primaryColorHex) {
            if (route.isEmpty()) "" else {
                val path = route.joinToString("|") { "${it.latitude},${it.longitude}" }
                "https://maps.googleapis.com/maps/api/staticmap?" +
                        "size=600x300&" +
                        "path=color:0x$primaryColorHex|weight:5|$path&" +
                        "key=AIzaSyCdDvb7RiCnc86jlFjJ8yJzIE4xTC8Fri8"
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = publication.user?.imageUrl
                        ?: "https://i.postimg.cc/RFSkJZtg/462076-1g-CSN462076-MG3928385-1248x702.webp",
                    contentDescription = "Imagen de perfil",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = publication.user?.username ?: "Usuario desconocido",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = publication.activityType?.icon
                                ?: Icons.Default.DirectionsRun,
                            contentDescription = publication.activityType?.name,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = publication.startTime?.format(
                                DateTimeFormatter.ofPattern("d MMM yyyy 'a las' HH:mm")
                            ) ?: "Fecha desconocida",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = publication.title ?: "Actividad sin título",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Distancia",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = feedViewModel.formatDistance(publication.distance),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp
                        ),
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Desnivel positivo",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = "${publication.positiveElevation.toInt()} m",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp
                        ),
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Tiempo",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp
                        )
                    )
                    Text(
                        text = feedViewModel.formatDuration(publication.duration),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp
                        ),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (mapUrl.isNotEmpty()) {
                Image(
                    painter = rememberImagePainter(mapUrl),
                    contentDescription = "Mapa de la ruta",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = {
                            try {
                                feedViewModel.toggleLike(
                                    publication.id ?: 0L,
                                    isLiked
                                )
                            } catch (e: Exception) {
                                Log.e("Publicacion", "Error al hacer like", e)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            modifier = Modifier.size(28.dp),
                            tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = likeCount.toString(),
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = { showComments = true }
                    ) {
                        Icon(
                            Icons.Default.Comment,
                            contentDescription = "Comment",
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Bottom Sheet de comentarios
                    if (showComments) {
                        ModalBottomSheet(
                            onDismissRequest =
                            {
                                feedViewModel.clearComentario()
                                showComments = false
                            },
                            sheetState = sheetState,
                            containerColor = MaterialTheme.colorScheme.background
                        ) {
                            CommentsSection(
                                publicationId = publication.id ?: 0L,
                                comments = comments,
                                comentario = comentario,
                                isLoading = isLoadingComments,
                                viewModel = feedViewModel
                            )
                        }

                        // Cargar comentarios cuando se abre el BottomSheet
                        LaunchedEffect(showComments) {
                            if (showComments) {
                                feedViewModel.loadComments(publication.id ?: 0L)
                            }
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = "Share",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommentsSection(
    publicationId: Long,
    comentario: String,
    comments: List<Comment>,
    isLoading: Boolean,
    viewModel: FeedViewModel,
) {
    val maxChar = 250
    val charCount = comentario.length
    val isOverLimit = charCount > maxChar

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Comentarios",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (comments.isEmpty()) {
            Text(
                text = "No hay comentarios",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxHeight(0.7f)
                    .padding(vertical = 8.dp)
            ) {
                itemsIndexed(comments) { _, comment ->
                    Divider()
                    CommentItem(
                        comment = comment,
                        feedViewModel = viewModel
                    )
                    Divider()
                }
            }
        }

        // Campo para añadir nuevo comentario
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = viewModel.imagenPerfil.value,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            TextField(
                value = comentario,
                onValueChange = { viewModel.onComentarioChange(it) },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = { Text("Escribe un comentario...") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                trailingIcon = {
                    Text(
                        text = "$charCount/$maxChar",
                        color = if (isOverLimit) Color.Red else Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            )

            IconButton(
                onClick = {
                    if (comentario.isNotBlank()) {
                        viewModel.addComment(publicationId)
                    }
                },
                enabled = comentario.isNotBlank() && !isOverLimit
            ) {
                Icon(Icons.Default.Send, contentDescription = "Enviar")
            }
        }
    }
}


@Composable
fun CommentItem(comment: Comment, feedViewModel: FeedViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = if (comment.userImage.isNotEmpty()) comment.userImage else "https://i.postimg.cc/RFSkJZtg/462076-1g-CSN462076-MG3928385-1248x702.webp",
            contentDescription = "Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(comment.userName, fontWeight = FontWeight.Bold)
            Text(comment.comment)
            Text(
                text = feedViewModel.tiempoTranscurrido(comment.creationDate),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}