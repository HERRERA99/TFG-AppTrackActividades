package com.aitor.trackactividades.core.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Enum que representa las distintas modalidades de actividad física soportadas por la aplicación.
 *
 * Cada modalidad incluye:
 * - Un ícono (`ImageVector`) representativo.
 * - Un nombre visible (`displayName`) que será mostrado al usuario.
 *
 * Modalidades disponibles:
 * - Ciclismo de Carretera
 * - Ciclismo de Montaña
 * - Caminata
 * - Correr
 *
 * Ejemplo de uso:
 * ```
 * val modalidad = Modalidades.CORRER
 * val nombre = modalidad.displayName // "Correr"
 * val icono = modalidad.icon         // Icons.Default.DirectionsRun
 * ```
 */
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