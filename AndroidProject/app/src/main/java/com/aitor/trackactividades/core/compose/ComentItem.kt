package com.aitor.trackactividades.core.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.aitor.trackactividades.feed.presentation.model.Comment
import com.aitor.trackactividades.perfil.presentation.PostInteractionViewModel

@Composable
fun CommentItem(
    comment: Comment,
    viewModel: PostInteractionViewModel,
    modifier: Modifier = Modifier
) {
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
                text = viewModel.tiempoTranscurrido(comment.creationDate),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}