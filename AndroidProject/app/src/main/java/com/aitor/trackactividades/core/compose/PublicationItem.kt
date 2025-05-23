package com.aitor.trackactividades.core.compose

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.aitor.trackactividades.feed.presentation.model.Publication
import com.aitor.trackactividades.perfil.presentation.PostInteractionViewModel
import java.time.format.DateTimeFormatter
import kotlin.collections.contains

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicationItem(
    publication: Publication,
    navigateToActivity: (Long) -> Unit,
    viewModel: PostInteractionViewModel?,
    modifier: Modifier = Modifier,
    navigateToProfile: (Int) -> Unit
) {
    // Estado para el Bottom Sheet
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var showComments by remember() { mutableStateOf(false) }

    // Observar el estado del ViewModel
    val isLiked by remember(publication.id) {
        derivedStateOf {
            viewModel?.isPublicationLiked(publication.likes, publication.id ?: 0L) ?: false
        }
    }


    // Contador de likes basado en el estado real
    val likeCount by remember(publication.likes, isLiked) {
        derivedStateOf {
            val baseCount = publication.likes.size
            if (isLiked && !publication.likes.contains(viewModel?.userId?.value)) {
                baseCount + 1
            } else if (!isLiked && publication.likes.contains(viewModel?.userId?.value)) {
                baseCount - 1
            } else {
                baseCount
            }
        }
    }
    if (viewModel != null) {
        // Observar los comentarios y el estado de carga

        val comments by viewModel.comments.collectAsState()
        val isLoadingComments by viewModel.isLoadingComments.collectAsState()
    }

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
                Image(
                    painter = rememberAsyncImagePainter(
                        model = publication.user?.imageUrl
                            ?: "https://i.postimg.cc/RFSkJZtg/462076-1g-CSN462076-MG3928385-1248x702.webp"
                    ),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.Gray, CircleShape)
                        .clickable {
                            navigateToProfile(publication.user?.id ?: 0)
                        },
                    contentScale = ContentScale.Crop
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
                        text = publication.formatDistance(),
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
                        text = publication.formatDuration(),
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

            if (viewModel != null) {
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
                                    viewModel.toggleLike(
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
                                        viewModel.clearComentario()
                                        showComments = false
                                    },
                                sheetState = sheetState,
                                containerColor = MaterialTheme.colorScheme.background
                            ) {
                                CommentsSection(
                                    publicationId = publication.id ?: 0L,
                                    viewModel = viewModel
                                )
                            }

                            // Cargar comentarios cuando se abre el BottomSheet
                            LaunchedEffect(showComments) {
                                if (showComments) {
                                    viewModel.loadComments(publication.id ?: 0L)
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
}