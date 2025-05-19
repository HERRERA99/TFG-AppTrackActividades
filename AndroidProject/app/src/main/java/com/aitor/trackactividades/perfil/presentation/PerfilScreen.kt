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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
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
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.rememberAsyncImagePainter
import com.aitor.trackactividades.core.compose.PublicationItem
import com.aitor.trackactividades.core.compose.PublicationsList
import com.aitor.trackactividades.feed.presentation.model.Publication
import com.aitor.trackactividades.perfil.presentation.model.UserModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navigateToHome: () -> Unit,
    navigateToActivity: (Long) -> Unit,
    perfilViewModel: PerfilViewModel,
    postInteractionViewModel: PostInteractionViewModel
) {
    val publications = perfilViewModel.publications.collectAsLazyPagingItems()
    val user by perfilViewModel.user.collectAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            PerfilTopBar(
                navigateToHome = navigateToHome,
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                PerfilContainer(
                    user = user,
                    onEditClick = {},
                    isCurrentUser = perfilViewModel.isCurrentUser(),
                    onFollowClick = {}
                )
            }

            items(publications.itemCount) { index ->
                val publication = publications[index]
                publication?.let {
                    PublicationItem(
                        publication = publication,
                        navigateToActivity = navigateToActivity,
                        viewModel = postInteractionViewModel,
                        navigateToProfile = {}
                    )
                }
            }

            if (publications.loadState.append is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (publications.loadState.refresh is LoadState.Error) {
                val error = publications.loadState.refresh as LoadState.Error
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error al cargar publicaciones", color = Color.Red)
                        Text(error.error.localizedMessage ?: "Error desconocido")
                        Button(onClick = { publications.retry() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilTopBar(
    navigateToHome: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
) {
    TopAppBar(
        title = {
            Text(
                text = "Perfil",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = navigateToHome) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(48.dp)
                )
            }
        },
        scrollBehavior = scrollBehavior,
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
    onFollowClick: () -> Unit
) {
    val genderIcon = when (user?.genero?.name?.lowercase()) {
        "masculino" -> Icons.Default.Male
        "femenino" -> Icons.Default.Female
        else -> Icons.Default.Person
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
    ) {
        user?.let {
            Column(modifier = Modifier.fillMaxSize()) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                ) {
                    // Imagen de fondo desplazada hacia abajo
                    Image(
                        painter = rememberAsyncImagePainter(model = user.image),
                        contentDescription = "Imagen de fondo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Imagen de perfil
                    Image(
                        painter = rememberAsyncImagePainter(model = user.image),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(130.dp)
                            .align(Alignment.BottomCenter)
                            .clip(CircleShape)
                            .border(5.dp, MaterialTheme.colorScheme.primaryContainer, CircleShape)
                            .zIndex(1f),
                        contentScale = ContentScale.Crop
                    )

                    // Botón en la parte superior derecha
                    if (isCurrentUser) {
                        CompactOutlinedButton(
                            onClick = onEditClick,
                            icon = Icons.Default.Edit,
                            text = "Editar",
                            contentDescription = "Editar",
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 12.dp, end = 12.dp)
                        )
                    } else {
                        val (icon, text, desc) = if (user.isFollowing) {
                            Triple(Icons.Default.Check, "Siguiendo", "Siguiendo")
                        } else {
                            Triple(Icons.Default.PersonAdd, "Seguir", "Seguir")
                        }

                        CompactOutlinedButton(
                            onClick = onFollowClick,
                            icon = icon,
                            text = text,
                            contentDescription = desc,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 12.dp, end = 12.dp)
                        )
                    }
                }



                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Nombre y username
                    Text(
                        text = "${user.nombre} ${user.apellidos}",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "@${user.username}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                        color = Color.Gray
                    )
                    Row (horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = genderIcon,
                            contentDescription = "Genero",
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${user.genero}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }

//                    Spacer(modifier = Modifier.height(16.dp))

                    // Estadísticas
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileStatItem(count = user.followersCount, label = "Seguidores")
                        ProfileStatItem(count = user.followingCount, label = "Seguidos")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
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
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(36.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.primary
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
        elevation = ButtonDefaults.elevatedButtonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp,
            disabledElevation = 0.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(21.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text, fontSize = 18.sp)
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