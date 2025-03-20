package com.aitor.trackactividades.recordActivity.presentation

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.Location
import android.os.SystemClock
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aitor.trackactividades.core.model.Modalidades
import com.aitor.trackactividades.core.userPreferences.UserPreferences
import com.aitor.trackactividades.recordActivity.presentation.model.ScreenTypes
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RecordActivityViewModel @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _filteredUserLocation = MutableLiveData<Location?>()
    val filteredUserLocation: LiveData<Location?> get() = _filteredUserLocation

    private val _screenMode = MutableLiveData<ScreenTypes>(ScreenTypes.START_ACTIVITY)
    val screenMode: LiveData<ScreenTypes> get() = _screenMode

    private val _routeCoordinates = MutableLiveData<List<LatLng>>(emptyList())
    val routeCoordinates: LiveData<List<LatLng>> get() = _routeCoordinates

    private val _stopwatch = MutableLiveData<Long>(0L)
    val stopwatch: LiveData<Long> get() = _stopwatch

    private val _speed = MutableLiveData<Float>(0f)
    val speed: LiveData<Float> get() = _speed

    private val _altitude = MutableLiveData<Double>(0.0)
    val altitude: LiveData<Double> get() = _altitude

    private val _calories = MutableLiveData<Float>(0f)
    val calories: LiveData<Float> get() = _calories

    private val _distance = MutableLiveData<Float>(0f)
    val distance: LiveData<Float> get() = _distance

    private val _activityType = MutableLiveData<Modalidades>(Modalidades.CICLISMO_CARRETERA)
    val activityType: LiveData<Modalidades> get() = _activityType

    private var isRunning = false
    private var isPaused = false
    private var startTime: Long = 0L
    private var elapsedTime: Long = 0L

    private var locationCallback: LocationCallback? = null
    private var lastLocation: Location? = null // Almacena la última ubicación registrada

    private var userWeight: Double = 70.0 // Peso por defecto

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

    fun setScreenMode(screenMode: ScreenTypes) {
        _screenMode.postValue(screenMode)
    }

    private fun getLocation(context: Context) {
        if (ActivityCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context, android.Manifest.permission.ACCESS_COARSE_LOCATION
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
                        _filteredUserLocation.postValue(location)
                        val latLng = LatLng(location.latitude, location.longitude)
                        _routeCoordinates.postValue(_routeCoordinates.value.orEmpty() + latLng)
                        _speed.postValue(location.speed * 3.6f)
                        _altitude.postValue(location.altitude)

                        Log.e("Calorias", "Calorias/s: ${calculateCalories(userWeight, _speed.value!!, _activityType.value!!, 1)} \n Calorias: ${_calories.value}")
                        _calories.postValue(_calories.value?.plus(calculateCalories(userWeight, _speed.value!!, _activityType.value!!, 1)))

                        lastLocation?.let { lastLoc ->
                            val distanceInMeters = lastLoc.distanceTo(location)
                            _distance.postValue(_distance.value?.plus(distanceInMeters) ?: distanceInMeters)
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

    fun calculateCalories(weight: Double, speed: Float, activityType: Modalidades, timeInSeconds: Int): Float {
        val met = when (activityType) {
            Modalidades.CICLISMO_CARRETERA, Modalidades.CICLISMO_MONTAÑA -> when {
                speed >= 30 -> 14.0f
                speed >= 25 -> 12.0f
                speed >= 22 -> 10.0f
                speed >= 17 -> 8.0f
                else -> 4.0f
            }
            Modalidades.CORRER, Modalidades.CAMINATA -> when {
                speed >= 19 -> 20.0f
                speed >= 16 -> 15.0f
                speed >= 12 -> 11.8f
                speed >= 10 -> 9.8f
                speed >= 8 -> 8.3f
                speed >= 6 -> 6.0f
                else -> 2.0f
            }
        }
        return ((met * weight.toFloat() * (timeInSeconds.toFloat()) * 3.5f) / (200.0f * 60))
    }

    private fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

    fun onTypeChange(type: Modalidades) {
        _activityType.postValue(type)
    }

    fun formatTime(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs)
    }

    fun speedConversor(speed: Float, modalidades: Modalidades): String {
        return when (modalidades) {
            Modalidades.CICLISMO_CARRETERA, Modalidades.CICLISMO_MONTAÑA -> {
                // Mostrar la velocidad en km/h sin conversión
                String.format(Locale.getDefault(), "%.2f km/h", speed)
            }
            Modalidades.CAMINATA, Modalidades.CORRER -> {
                // Convertir de km/h a min/km
                val minKm = if (speed > 0) 60f / speed else 0f
                String.format(Locale.getDefault(), "%.2f min/km", minKm)
            }
        }
    }
}
