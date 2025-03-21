package com.aitor.trackactividades.recordActivity.presentation.utils

import java.util.Locale

object FormatTime {
    fun formatTime(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs)
    }
}