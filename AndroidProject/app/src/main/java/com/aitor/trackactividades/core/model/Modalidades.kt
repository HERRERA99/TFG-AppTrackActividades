package com.aitor.trackactividades.core.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.ui.graphics.vector.ImageVector

enum class Modalidades(
    val icon: ImageVector,
    val displayName: String
) {
    CICLISMO_CARRETERA(
        icon = Icons.Default.DirectionsBike,
        displayName = "Ciclismo de Carretera"
    ),
    CICLISMO_MONTAÑA(
        icon = Icons.Default.DirectionsBike,
        displayName = "Ciclismo de Montaña"
    ),
    CAMINATA(
        icon = Icons.Default.DirectionsWalk,
        displayName = "Caminata"
    ),
    CORRER(
        icon = Icons.Default.DirectionsRun,
        displayName = "Correr"
    )
}