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
    var velocidadMedia: Double? = null,
    var calorias: Double? = null,
    var velocidadMaxima: Double? = null,
    val velocidades: MutableList<Double> = mutableListOf(),
    val desniveles: MutableList<Double> = mutableListOf(),
    var altitudMaxima: Double? = null,
    val ruta: MutableList<LatLng> = mutableListOf(),
    val titulo: String = GeneradorNombre.nombreAutomatico(horaInicio, tipoActividad)
) {

    fun agregarDatos(calorias: Double, velocidad: Double, desnivel: Double, coordenada: LatLng) {
        velocidades.add(velocidad)
        desniveles.add(desnivel)
        ruta.add(coordenada)
        this.calorias = calorias
    }

    fun terminarActividad(distancia: Double?) {
        horaFin = LocalDateTime.now()

        // Calcular duración en segundos
        duracion = java.time.Duration.between(horaInicio, horaFin).seconds

        // Calcular distancia total (suma de las distancias entre puntos de la ruta)
        this.distancia = distancia

        // Calcular velocidad media (distancia / duración en horas)
        velocidadMedia = if (duracion!! > 0) this.distancia!! / (duracion!! / 3600.0) else 0.0

        // Calcular velocidad máxima
        velocidadMaxima = velocidades.maxOrNull() ?: 0.0

        // Calcular desnivel positivo (suma de los desniveles positivos)
        desnivelPositivo = desniveles.filter { it > 0 }.sum()

        // Calcular altitud máxima
        altitudMaxima = desniveles.maxOrNull() ?: 0.0
    }
}

object GeneradorNombre {
    fun nombreAutomatico(horaInicio: LocalDateTime, tipoActividad: Modalidades): String {
        val hora = horaInicio.hour // Obtener la hora del día (0-23)
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
