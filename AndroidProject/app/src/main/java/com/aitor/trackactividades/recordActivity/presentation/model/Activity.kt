import GeneradorNombre.nombreAutomatico
import android.os.Build
import androidx.annotation.RequiresApi
import com.aitor.trackactividades.core.model.Modalidades
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

data class Activity(
    val id: Long = System.currentTimeMillis(),
    val horaInicio: LocalDateTime,
    val tipoActividad: Modalidades,
    var horaFin: LocalDateTime? = null,
    var distancia: Double? = null,
    var duracion: Long? = null,
    var desnivelPositivo: Double? = null,
    var velocidadMedia: Float? = null,
    var calorias: Double? = null,
    var velocidadMaxima: Float? = null,
    val velocidades: MutableList<Float> = mutableListOf(),
    val desniveles: MutableList<Double> = mutableListOf(),
    var altitudMaxima: Double? = null,
    val ruta: MutableList<LatLng> = mutableListOf(),
    var titulo: String = nombreAutomatico(horaInicio, tipoActividad)
) {
    fun agregarDatos(velocidad: Float, desnivel: Double, coordenada: LatLng) {
        velocidades.add(velocidad)
        desniveles.add(desnivel)
        ruta.add(coordenada)
    }

    fun terminarActividad(distancia: Double?, calorias: Double?) {
        horaFin = LocalDateTime.now()
        duracion = java.time.Duration.between(horaInicio, horaFin).seconds
        this.distancia = distancia
        velocidadMedia = velocidades.average().toFloat()
        desnivelPositivo = desniveles.sumOf { if (it > 0) it else 0.0 }
        velocidadMaxima = velocidades.maxOrNull() ?: 0.0f
        altitudMaxima = desniveles.maxOrNull() ?: 0.0
        this.calorias = calorias
    }
}

object GeneradorNombre {
    fun nombreAutomatico(horaInicio: LocalDateTime, tipoActividad: Modalidades): String {
        val hora = horaInicio.hour
        val parteDelDia = when (hora) {
            in 6..11 -> "por la mañana"
            in 12..17 -> "por la tarde"
            in 18..21 -> "por la noche"
            in 22..23 -> "por la noche"
            in 0..5 -> "al amanecer"
            else -> ""
        }
        return "${tipoActividad.displayName} $parteDelDia"
    }
}