package com.aitor.trackactividades.feed.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FeedScreen(
    feedViewModel: FeedViewModel,
    navigateToStartRecordActivity: () -> Unit
) {
    Scaffold(
        topBar = { FeedTopBar() },
        bottomBar = { FeedBottomBar(navigateToStartRecordActivity) }
    ) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Text(
                        text = "Aquí irán las publicaciones...",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedTopBar() {
    TopAppBar(
        title = {
            Text(
                text = "Inicio",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        actions = {
            IconButton(onClick = { /* TODO: Acción de notificaciones */ }) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones",
                    modifier = Modifier.size(32.dp)
                )
            }
            IconButton(onClick = { /* TODO: Acción de perfil */ }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Perfil",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    )
}

@Composable
fun FeedBottomBar( navigateToStartRecordActivity: () -> Unit) {
    NavigationBar {
        BottomNavItem("Inicio", Icons.Default.Home) { /* TODO: Navegar a Inicio */ }
        BottomNavItem("Mapa", Icons.Default.Map) { /* TODO: Navegar a Mapa */ }
        BottomNavItem("Registrar", Icons.Default.AddCircle) { navigateToStartRecordActivity() }
        BottomNavItem("Grupos", Icons.Default.Group) { /* TODO: Navegar a Grupos */ }
        BottomNavItem("Tú", Icons.Default.Person) { /* TODO: Navegar a Tú */ }
    }
}

@Composable
fun BottomNavItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    NavigationRailItem(
        icon = {
            Icon(
                icon,
                contentDescription = label,
                modifier = Modifier.size(32.dp)
            )
        },
        label = {
            Text(
                label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        },
        selected = false,
        onClick = onClick
    )
}
