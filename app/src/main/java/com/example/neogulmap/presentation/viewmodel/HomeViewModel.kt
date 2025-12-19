package com.example.neogulmap.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neogulmap.domain.model.Zone
import com.example.neogulmap.domain.usecase.GetZonesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.*

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getZonesUseCase: GetZonesUseCase
) : ViewModel() {

    private val _zones = MutableStateFlow<List<Zone>>(emptyList())
    val zones: StateFlow<List<Zone>> = _zones.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        Log.d("HomeViewModel", "init called, loading zones.")
        loadZones()
    }

    fun loadZones(latitude: Double = 0.0, longitude: Double = 0.0, radius: Int = 1000) {
        viewModelScope.launch {
            Log.d("HomeViewModel", "loadZones started with lat=$latitude, lon=$longitude, radius=$radius")
            _isLoading.value = true
            _errorMessage.value = null
            getZonesUseCase(latitude, longitude, radius).collect { result ->
                Log.d("HomeViewModel", "Collecting result: $result")
                result.onSuccess { allZones ->
                    val filteredZones = if (latitude != 0.0 || longitude != 0.0) { // Only filter if a valid location is provided
                        allZones.filter { zone ->
                            calculateDistance(latitude, longitude, zone.latitude, zone.longitude) <= radius
                        }
                    } else {
                        allZones // If lat/lon are default, show all zones
                    }
                    Log.d("HomeViewModel", "Zones loaded successfully: ${filteredZones.size} zones (filtered)")
                    _zones.value = filteredZones
                    _isLoading.value = false
                }.onFailure { e ->
                    val msg = "Failed to load zones: ${e.message}"
                    Log.e("HomeViewModel", msg, e)
                    _errorMessage.value = msg
                    _isLoading.value = false
                }
            }
            Log.d("HomeViewModel", "loadZones finished collecting.")
        }
    }

    // Haversine formula for distance calculation (more accurate than planar, but still an approximation)
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371000.0 // Earth's mean radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c // Distance in meters
    }
}