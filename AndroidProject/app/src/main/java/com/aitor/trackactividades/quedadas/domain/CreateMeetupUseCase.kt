package com.aitor.trackactividades.quedadas.domain

import android.content.Context
import android.net.Uri
import com.aitor.trackactividades.core.model.Modalidades
import com.aitor.trackactividades.quedadas.data.QuedadasRepository
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime
import java.time.OffsetDateTime
import javax.inject.Inject

class CreateMeetupUseCase @Inject constructor(private val quedadasRepository: QuedadasRepository) {
    suspend operator fun invoke(
        title: String,
        description: String?,
        dateTime: LocalDateTime,
        location: String,
        locationCoordinates: LatLng,
        sportType: Modalidades,
        gpxUri: Uri?,
        context: Context
        ): Result<Boolean> {
        return try {
            // Verificar primero el GPX si existe
            gpxUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes == null || bytes.isEmpty()) {
                    return Result.failure(Exception("El archivo GPX está vacío o no se pudo leer"))
                }

                // Opcional: Validar estructura básica del GPX
                val gpxContent = String(bytes)
                if (!gpxContent.contains("<trkpt") || !gpxContent.contains("</gpx>")) {
                    return Result.failure(Exception("El archivo no es un GPX válido"))
                }
            }

            // Si el GPX es válido o no hay GPX, proceder con la creación
            quedadasRepository.createMeetup(
                title,
                description,
                dateTime,
                location,
                locationCoordinates,
                sportType,
                gpxUri,
                context
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}