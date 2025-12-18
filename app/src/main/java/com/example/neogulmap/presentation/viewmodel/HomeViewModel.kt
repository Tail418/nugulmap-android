package com.example.neogulmap.presentation.viewmodel

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
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadZones()
    }

    fun loadZones() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val result = getZonesUseCase()
                result.onSuccess { zoneList ->
                    _zones.value = zoneList
                }.onFailure { e ->
                    _errorMessage.value = "Failed to load zones: ${e.message}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Failed to load zones: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}