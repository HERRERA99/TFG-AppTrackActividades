package com.aitor.trackactividades.core.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aitor.trackactividades.perfil.presentation.PostInteractionViewModel
import kotlin.text.isNotBlank

@Composable
fun CommentsSection(
    publicationId: Long,
    viewModel: PostInteractionViewModel,
    modifier: Modifier = Modifier
) {
    val comentario by viewModel.comentario.observeAsState("")
    val comments by viewModel.comments.collectAsState()
    val isLoading by viewModel.isLoadingComments.collectAsState()

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
                        viewModel = viewModel
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