package com.aitor.trackactividades.recordActivity.presentation

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
import com.aitor.trackactividades.core.model.Modalidades
import com.aitor.trackactividades.core.userPreferences.UserPreferences
import com.aitor.trackactividades.recordActivity.presentation.model.Activity
import com.aitor.trackactividades.recordActivity.presentation.model.ScreenTypes
import com.aitor.trackactividades.recordActivity.presentation.utils.CaloriesManager.calculateCalories
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class RecordActivityViewModel @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val userPreferences: UserPreferences
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
                        actualizaDashboard(location)
                        actualizaActividad()
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
        _userLocation.postValue(location)
        val latLng = LatLng(location.latitude, location.longitude)
        _routeCoordinates.postValue(_routeCoordinates.value.orEmpty() + latLng)
        _speed.postValue(location.speed * 3.6f)
        _calories.postValue(_calories.value?.plus(calculateCalories(userWeight, _speed.value!!, _activityType.value!!, 1)))
        lastLocation?.let { lastLoc ->
            val distanceInMeters = lastLoc.distanceTo(location)
            _distance.postValue(_distance.value?.plus(distanceInMeters) ?: distanceInMeters)
        }
        lastLocation = location
    }

    fun actualizaActividad() {

    }
}
