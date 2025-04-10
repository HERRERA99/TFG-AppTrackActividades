import com.aitor.trackactividades.core.model.Modalidades
import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

data class Activity(
    val id: Long,
    val horaInicio: LocalDateTime,
    val tipoActividad: Modalidades,
    var horaFin: LocalDateTime,
    var distancia: Float,
    var duracion: Long,
    var desnivelPositivo: Double,
    var velocidadMedia: Float,
    var calorias: Float,
    var velocidadMaxima: Float,
    val velocidades: List<Float>,
    val desniveles: List<Double>,
    var altitudMaxima: Double,
    val ruta: List<LatLng>,
    val distances: List<Float>,
    var titulo: String,
    var isPublic: Boolean
)