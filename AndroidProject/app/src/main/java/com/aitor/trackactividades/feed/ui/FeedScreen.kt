package com.aitor.trackactividades.feed.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun FeedScreen(feedViewModel: FeedViewModel) {
    Scaffold { innerPadding ->
        Box(Modifier.padding(innerPadding).fillMaxSize()) {
            Text(text = "Feed", modifier = Modifier.align(Alignment.Center))
        }
    }
}