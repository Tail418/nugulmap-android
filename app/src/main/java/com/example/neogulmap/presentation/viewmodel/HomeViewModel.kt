package com.example.neogulmap.presentation.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neogulmap.domain.model.Zone
import com.example.neogulmap.domain.usecase.GetZonesUseCase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(val zones: List<Zone>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getZonesUseCase: GetZonesUseCase,
    private val application: Application // Inject Application context for FusedLocationProviderClient
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _currentLocation = MutableStateFlow<LatLng?>(null)
    val currentLocation = _currentLocation.asStateFlow()

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application.applicationContext)

    init {
        // Load initial zones for a default location (e.g., Seoul City Hall)
        loadZones(latitude = 37.5665, longitude = 126.9780)
    }

    fun loadZones(latitude: Double, longitude: Double, radius: Int = 1000) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            getZonesUseCase(latitude, longitude, radius)
                .onSuccess { zones ->
                    _uiState.value = HomeUiState.Success(zones)
                }
                .onFailure { error ->
                    _uiState.value = HomeUiState.Error(error.message ?: "Failed to load zones.")
                }
        }
    }

    @SuppressLint("MissingPermission")
    fun moveToCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                _currentLocation.value = LatLng(it.latitude, it.longitude)
                // Optionally reload zones around the current location
                loadZones(it.latitude, it.longitude)
            } ?: run {
                // Handle case where last location is null, e.g., request new location updates
                // For simplicity, we'll just log for now
                println("Last known location is null.")
            }
        }
    }
}
