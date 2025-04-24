package com.aitor.trackactividades.perfil.presentation

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.aitor.trackactividades.core.compose.PublicationsList
import com.aitor.trackactividades.feed.presentation.model.Publication
import com.aitor.trackactividades.perfil.presentation.model.UserModel
import kotlinx.coroutines.launch

@Composable
fun PerfilScreen(
    navigateToHome: () -> Unit,
    navigateToActivity: (Long) -> Unit,
    perfilViewModel: PerfilViewModel,
    postInteractionViewModel: PostInteractionViewModel
) {
    val publications = perfilViewModel.publications.collectAsLazyPagingItems()
    val user by perfilViewModel.user.observeAsState()

    Scaffold(
        topBar = {
            PerfilTopBar() {
                navigateToHome()
            }
        }
    ) { innerPading ->
        Column(
            Modifier
                .padding(innerPading)
                .fillMaxSize()
        ) {
            PerfilContainer(
                user = user,
                modifier = Modifier.weight(0.3f),
                onEditClick = {},
                onShareClick = {},
                isCurrentUser = perfilViewModel.isCurrentUser(perfilViewModel.userId.value ?: 0),
                onFollowClick = {}
            )
            PublicationsContainer(
                modifier = Modifier.weight(0.7f),
                publications = publications,
                navigateToActivity = { publicationId ->
                    navigateToActivity(publicationId)
                },
                postInteractionViewModel = postInteractionViewModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilTopBar(navigatetoHome: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Perfil",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        },actions = {
            IconButton(onClick = {
                navigatetoHome
            }) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
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
fun PerfilContainer(
    user: UserModel?,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier,
    onEditClick: () -> Unit,
    onFollowClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
    ) {
        user?.let {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Fila con foto y nombres
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp)
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = user.image),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.Gray, CircleShape),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "${user.nombre} ${user.apellidos}",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 22.sp)
                        )
                        Text(
                            text = "@${user.username}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Fila con stats y botones
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        ProfileStatItem(count = user.followersCount, label = "Seguidores")
                        Spacer(modifier = Modifier.width(16.dp))
                        ProfileStatItem(count = user.followingCount, label = "Seguidos")
                    }

                    Row {
                        CompactOutlinedButton(
                            onClick = onShareClick,
                            icon = Icons.Default.Share,
                            text = "Compartir",
                            contentDescription = "Compartir"
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        if (isCurrentUser) {
                            CompactOutlinedButton(
                                onClick = onEditClick,
                                icon = Icons.Default.Edit,
                                text = "Editar perfil",
                                contentDescription = "Editar"
                            )
                        } else {
                            if (user.isFollowing) {
                                FilledFollowButton(
                                    onClick = onFollowClick,
                                    icon = Icons.Default.Check,
                                    text = "Siguiendo",
                                    contentDescription = "Siguiendo"
                                )
                            } else {
                                CompactOutlinedButton(
                                    onClick = onFollowClick,
                                    icon = Icons.Default.PersonAdd,
                                    text = "Seguir",
                                    contentDescription = "Seguir"
                                )
                            }
                        }

                    }

                }
            }
        }
    }
}

@Composable
fun CompactOutlinedButton(
    onClick: () -> Unit,
    icon: ImageVector,
    text: String,
    contentDescription: String
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(36.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, fontSize = 13.sp)
        }
    }
}

@Composable
fun FilledFollowButton(
    onClick: () -> Unit,
    icon: ImageVector,
    text: String,
    contentDescription: String
) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(36.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, fontSize = 13.sp)
        }
    }
}




@Composable
fun ProfileStatItem(count: Int, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}



@Composable
fun PublicationsContainer(
    modifier: Modifier = Modifier,
    publications: LazyPagingItems<Publication>,
    navigateToActivity: (Long) -> Unit,
    postInteractionViewModel: PostInteractionViewModel
) {
    Box(modifier = modifier.fillMaxSize()) {
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
                    viewModel = postInteractionViewModel
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