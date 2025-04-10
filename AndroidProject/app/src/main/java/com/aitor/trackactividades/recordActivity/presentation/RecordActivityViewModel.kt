package com.aitor.trackactividades.recordActivity.presentation

import Activity
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.SystemClock
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitor.trackactividades.authentication.presentation.model.LoginModel
import com.aitor.trackactividades.core.model.Modalidades
import com.aitor.trackactividades.core.token.TokenManager
import com.aitor.trackactividades.core.userPreferences.UserPreferences
import com.aitor.trackactividades.recordActivity.domain.SaveActivityUseCase
import com.aitor.trackactividades.recordActivity.domain.SavePublicationUseCase
import com.aitor.trackactividades.recordActivity.presentation.model.ScreenTypes
import com.aitor.trackactividades.recordActivity.presentation.utils.CaloriesManager.calculateCalories
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class RecordActivityViewModel @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val userPreferences: UserPreferences,
    private val tokenManager: TokenManager,
    private val saveActivityUseCase: SaveActivityUseCase,
    private val savePublicationUseCase: SavePublicationUseCase
) : ViewModel() {

    private val _userLocation = MutableLiveData<Location?>()
    val userLocation: LiveData<Location?> get() = _userLocation

    private val _screenMode = MutableLiveData<ScreenTypes>(ScreenTypes.START_ACTIVITY)
    val screenMode: LiveData<ScreenTypes> get() = _screenMode

    private val _routeCoordinates = MutableLiveData<List<LatLng>>(emptyList())
    val routeCoordinates: LiveData<List<LatLng>> get() = _routeCoordinates

    private val _stopwatch = MutableLiveData<Long>(0L)
    val stopwatch: LiveData<Long> get() = _stopwatch

    private val _speed = MutableLiveData<Float>(0f)
    val speed: LiveData<Float> get() = _speed

    private val _calories = MutableLiveData<Float>(0f)
    val calories: LiveData<Float> get() = _calories

    private val _distance = MutableLiveData<Float>(0f)
    val distance: LiveData<Float> get() = _distance

    private val _activityType = MutableLiveData<Modalidades>(Modalidades.CICLISMO_CARRETERA)
    val activityType: LiveData<Modalidades> get() = _activityType

    private val _activityTitle = MutableLiveData<String>()
    val activityTitle: LiveData<String> get() = _activityTitle

    private var isRunning = false
    private var isPaused = false
    private var startTime: Long = 0L
    private var elapsedTime: Long = 0L
    private var startUserLocation: Location? = null

    private var locationCallback: LocationCallback? = null
    private var lastLocation: Location? = null // Almacena la última ubicación registrada

    private var userWeight: Double = 70.0 // Peso por defecto

    // Variables para registrar la actividad
    private var actividad: Activity? = null
    private lateinit var startTimeActivity: LocalDateTime
    private var velocidades: MutableList<Float> = mutableListOf()
    private var altitudes: MutableList<Double> = mutableListOf()
    private var distances: MutableList<Float> = mutableListOf()
    private var visibility: Boolean = true

    init {
        viewModelScope.launch {
            userPreferences.userFlow.first()?.let {
                userWeight = it.peso
            }
        }
    }

    fun start(context: Context) {
        if (isRunning) return
        setScreenMode(ScreenTypes.RECORD_ACTIVITY)
        isRunning = true
        isPaused = false
        startTime = SystemClock.elapsedRealtime() - elapsedTime
        startTimeActivity = LocalDateTime.now()
        _activityTitle.postValue(nombreAutomatico(startTimeActivity, _activityType.value!!))
        startUserLocation = userLocation.value

        viewModelScope.launch {
            while (isRunning) {
                if (!isPaused) {
                    val currentTime = SystemClock.elapsedRealtime()
                    _stopwatch.postValue(currentTime - startTime)
                    getLocation(context)
                }
                delay(1000)
            }
        }
    }

    fun pause() {
        isPaused = true
        elapsedTime = SystemClock.elapsedRealtime() - startTime
        setScreenMode(ScreenTypes.PAUSE_ACTIVITY)
    }

    fun resume() {
        isPaused = false
        startTime = SystemClock.elapsedRealtime() - elapsedTime
        setScreenMode(ScreenTypes.RECORD_ACTIVITY)
    }

    fun reset() {
        isRunning = false
        isPaused = false
        elapsedTime = 0L
        startTime = 0L
        _stopwatch.postValue(0L)
        stopLocationUpdates()
    }

    fun save() {
        Log.d("Titulo", _activityTitle.value!!)
        viewModelScope.launch {
            actividad = Activity(
                id = System.currentTimeMillis(),
                horaInicio = startTimeActivity,
                tipoActividad = _activityType.value!!,
                horaFin = LocalDateTime.now(),
                distancia = _distance.value!!,
                duracion = _stopwatch.value!!,
                desnivelPositivo = altitudes.sumOf { if (it > 0) it else 0.0 },
                velocidadMedia = velocidades.average().toFloat(),
                calorias = _calories.value!!,
                velocidadMaxima = velocidades.maxOrNull() ?: 0.0f,
                velocidades = velocidades,
                desniveles = altitudes,
                altitudMaxima = altitudes.maxOrNull() ?: 0.0,
                ruta = _routeCoordinates.value!!,
                titulo = if (_activityTitle.value!!.isNotEmpty()) _activityTitle.value!! else nombreAutomatico(startTimeActivity, _activityType.value!!),
                distances = distances,
                isPublic = visibility
            )
            try {
                saveActivityUseCase(
                    "Bearer ${tokenManager.getToken()}",
                    actividad!!
                )
                savePublicationUseCase(
                    "Bearer ${tokenManager.getToken()}",
                    actividad?.id!!
                )
            } catch (e: HttpException) {
                Log.e("Error", e.message())
            }
        }
    }

    fun discard() {
        // Detener las actualizaciones de ubicación
        stopLocationUpdates()

        // Reiniciar todos los LiveData y variables de estado
        _stopwatch.postValue(0L)
        _distance.postValue(0f)
        _speed.postValue(0f)
        _calories.postValue(0f)
        _routeCoordinates.postValue(emptyList())
        _userLocation.postValue(null)
        _activityTitle.postValue("")

        // Reiniciar variables temporales
        isRunning = false
        isPaused = false
        startTime = 0L
        elapsedTime = 0L
        lastLocation = null
        velocidades.clear()
        altitudes.clear()
        distances.clear()
        actividad = null
        startTimeActivity = LocalDateTime.now()
        visibility = true
    }

    fun setScreenMode(screenMode: ScreenTypes) {
        _screenMode.postValue(screenMode)
    }

    private fun getLocation(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e("Location", "No tienes permisos de ubicación")
            return
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setMinUpdateIntervalMillis(500)
            .setWaitForAccurateLocation(true)
            .build()

        if (locationCallback == null) {
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        _userLocation.postValue(location)
                        if (!isPaused) {
                            actualizaDashboard(location)
                        }
                        lastLocation = location
                    }
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            null
        )
    }

    private fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

    fun onTypeChange(type: Modalidades) {
        _activityType.postValue(type)
    }

    fun actualizaDashboard(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        _routeCoordinates.postValue(_routeCoordinates.value.orEmpty() + latLng)
        _speed.postValue(location.speed * 3.6f)
        velocidades.add(location.speed * 3.6f)
        altitudes.add(location.altitude)
        _calories.postValue(
            _calories.value?.plus(
                calculateCalories(
                    userWeight,
                    _speed.value!!,
                    _activityType.value!!,
                    1
                )
            )
        )
        lastLocation?.let { lastLoc ->
            val distanceInMeters = lastLoc.distanceTo(location)
            val distanceAux = _distance.value?.plus(distanceInMeters)
            _distance.postValue(distanceAux ?: distanceInMeters)
            val distanceInKm = (distanceAux?.div(1000.0)).let {
                String.format("%.1f", it).replace(",", ".").toFloat()
            }
            distances.add(distanceInKm)
        }
    }

    fun setActivityTitle(title: String) {
        actividad?.titulo = title
        _activityTitle.postValue(title)
    }

    fun setVisibility(visibility: Boolean) {
        this.visibility = visibility
    }

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