package com.aitor.trackactividades.recordActivity.presentation

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
import com.aitor.trackactividades.recordActivity.presentation.model.ScreenTypes
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class RecordActivityViewModel @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient
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

    private val _altitude = MutableLiveData<List<Float>>(emptyList())
    val altitude: LiveData<List<Float>> get() = _altitude

    private val _slope = MutableLiveData<List<Float>>(emptyList())
    val slope: LiveData<List<Float>> get() = _slope

    private val _distance = MutableLiveData<Float>(0f)
    val distance: LiveData<Float> get() = _distance

    private var isRunning = false
    private var isPaused = false
    private var startTime: Long = 0L
    private var elapsedTime: Long = 0L

    private var locationCallback: LocationCallback? = null
    private var lastLocation: Location? = null // Almacena la última ubicación registrada


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

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).apply {
            setMinUpdateIntervalMillis(500)
        }.build()

        if (locationCallback == null) {
            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        _filteredUserLocation.postValue(location)
                        val latLng = LatLng(location.latitude, location.longitude)
                        _routeCoordinates.postValue(_routeCoordinates.value.orEmpty() + latLng)
                        _speed.postValue(location.speed * 3.6f) // Convertir m/s a km/h

                        // Calcular la distancia
                        lastLocation?.let { lastLoc ->
                            val distanceInMeters = lastLoc.distanceTo(location) // Distancia en metros
                            _distance.postValue(_distance.value?.plus(distanceInMeters) ?: distanceInMeters)
                        }

                        // Actualizar la última ubicación
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

    fun formatTime(milliseconds: Long): String {
        val seconds = milliseconds / 1000
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs)
    }
}
