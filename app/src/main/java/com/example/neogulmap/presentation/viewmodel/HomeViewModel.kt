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
                result.onSuccess { zoneList ->
                    Log.d("HomeViewModel", "Zones loaded successfully: ${zoneList.size} zones")
                    _zones.value = zoneList
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
}