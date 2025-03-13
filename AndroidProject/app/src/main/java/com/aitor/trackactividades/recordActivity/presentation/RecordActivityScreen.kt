package com.aitor.trackactividades.recordActivity.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RecordActivityScreen(
    recordActivityViewModel: RecordActivityViewModel
) {
    var elapsedTime by remember { mutableStateOf("00:00:00") }
    var speed by remember { mutableStateOf("0 km/h") }
    var distance by remember { mutableStateOf("0.00 km") }

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                IconButton(
                    onClick = {

                    },
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Accion",
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StatItem(title = "Tiempo", value = elapsedTime)
            Divider(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 48.dp)
            )
            StatItem(title = "Velocidad", value = speed)
            Divider(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                thickness = 1.dp,
                modifier = Modifier.padding(top = 48.dp)
            )
            StatItem(title = "Distancia", value = distance)
        }
    }
}

@Composable
fun StatItem(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            fontSize = 64.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
}