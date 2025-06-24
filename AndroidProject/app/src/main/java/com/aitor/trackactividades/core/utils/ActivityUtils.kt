package com.aitor.trackactividades.core.utils

fun formatSeconds(
    x: Double,
): String {
    val totalSeconds = x.toInt()

    return when {
        totalSeconds >= 3600 -> {
            val hours = totalSeconds / 3600
            val remainingMinutes = (totalSeconds % 3600) / 60
            val remainingSeconds = totalSeconds % 60

            if (remainingMinutes > 0 || remainingSeconds > 0) {
                "${hours}h ${remainingMinutes}min ${remainingSeconds}s"
            } else {
                "${hours}h"
            }
        }
        totalSeconds >= 60 -> {
            val minutes = totalSeconds / 60
            val remainingSeconds = totalSeconds % 60

            if (remainingSeconds > 0) {
                "${minutes}min ${remainingSeconds}s"
            } else {
                "${minutes}min"
            }
        }
        else -> "${totalSeconds}s"
    }
}