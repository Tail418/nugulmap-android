package com.example.neogulmap.presentation.viewmodel

import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neogulmap.domain.model.Zone
import com.example.neogulmap.domain.repository.ZoneRepository
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.Locale
import javax.inject.Inject

data class AddLocationFormState(
    val address: String = "",
    val region: String = "",
    val type: String = "",
    val subtype: String = "",
    val latitude: Double = 37.5665, // Default Seoul coordinates
    val longitude: Double = 126.9780, // Default Seoul coordinates
    val size: String = "",
    val description: String = "",
    val imageUri: Uri? = null,
    val isLoading: Boolean = false,
    val isGeocoding: Boolean = false,
    val showSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddLocationViewModel @Inject constructor(
    private val zoneRepository: ZoneRepository,
    @ApplicationContext private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient // Inject FusedLocationProviderClient
) : ViewModel() {

    private val _formState = MutableStateFlow(AddLocationFormState())
    val formState: StateFlow<AddLocationFormState> = _formState

    fun onAddressChange(address: String) {
        _formState.value = _formState.value.copy(address = address)
    }

    fun onRegionChange(region: String) {
        _formState.value = _formState.value.copy(region = region)
    }

    fun onTypeChange(type: String) {
        _formState.value = _formState.value.copy(type = type)
    }

    fun onSubtypeChange(subtype: String) {
        _formState.value = _formState.value.copy(subtype = subtype)
    }

    fun onSizeChange(size: String) {
        _formState.value = _formState.value.copy(size = size)
    }

    fun onDescriptionChange(description: String) {
        _formState.value = _formState.value.copy(description = description)
    }

    fun onImageUriChange(uri: Uri?) {
        _formState.value = _formState.value.copy(imageUri = uri)
    }

    fun setCoordinates(latitude: Double, longitude: Double) {
        _formState.value = _formState.value.copy(latitude = latitude, longitude = longitude)
    }

    fun setError(message: String?) {
        _formState.value = _formState.value.copy(error = message)
    }

    fun clearForm() {
        _formState.value = AddLocationFormState()
    }

    fun createZone(userId: String) {
        _formState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                // Perform validation similar to Next.js formSchema
                if (_formState.value.address.isBlank() || _formState.value.region.isBlank() ||
                    _formState.value.type.isBlank() || _formState.value.size.isBlank()) {
                    _formState.update { it.copy(error = "모든 필수 정보를 입력해주세요.", isLoading = false) }
                    return@launch
                }

                val newZone = zoneRepository.createZone(
                    latitude = _formState.value.latitude,
                    longitude = _formState.value.longitude,
                    name = _formState.value.address, // Using address as name, consistent with Next.js example description
                    address = _formState.value.address,
                    type = _formState.value.type,
                    userId = userId,
                    imageUri = _formState.value.imageUri
                )
                
                newZone.onSuccess { zone ->
                    Log.d("AddLocationViewModel", "Zone created successfully: $zone")
                    _formState.update { it.copy(isLoading = false, showSuccess = true) }
                    kotlinx.coroutines.delay(1500) // Show success for a short period
                    _formState.update { it.copy(showSuccess = false) }
                    clearForm() // Clear form after success
                }.onFailure { exception ->
                    val errorMessage = exception.message ?: "흡연구역 생성에 실패했습니다."
                    Log.e("AddLocationViewModel", "Error creating zone: $errorMessage", exception)
                    _formState.update { it.copy(isLoading = false, error = errorMessage) }
                }

            } catch (e: Exception) {
                val errorMessage = e.message ?: "알 수 없는 오류가 발생했습니다."
                Log.e("AddLocationViewModel", "Exception creating zone: $errorMessage", e)
                _formState.update { it.copy(isLoading = false, error = errorMessage) }
            }
        }
    }

    fun searchAddress() {
        val addressToSearch = _formState.value.address
        if (addressToSearch.isBlank()) {
            _formState.update { it.copy(error = "주소를 입력해주세요.") }
            return
        }

        viewModelScope.launch {
            _formState.update { it.copy(isGeocoding = true, error = null) }
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val addresses = geocoder.getFromLocationName(addressToSearch, 1)

                if (!addresses.isNullOrEmpty()) {
                    val location = addresses[0]
                    _formState.update { currentState ->
                        currentState.copy(
                            latitude = location.latitude,
                            longitude = location.longitude,
                            error = null
                        )
                    }
                } else {
                    _formState.update { it.copy(error = "입력하신 주소를 찾을 수 없습니다.") }
                }
            } catch (e: IOException) {
                val errorMessage = "주소 검색 중 네트워크 오류가 발생했습니다."
                Log.e("AddLocationViewModel", "Geocoding network error: $errorMessage", e)
                _formState.update { it.copy(error = errorMessage) }
            } catch (e: Exception) {
                val errorMessage = "주소 검색 중 오류가 발생했습니다."
                Log.e("AddLocationViewModel", "Geocoding error: $errorMessage", e)
                _formState.update { it.copy(error = errorMessage) }
            } finally {
                _formState.update { it.copy(isGeocoding = false) }
            }
        }
    }

    // Requires ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permissions
    // Also requires handling location settings and user consent
    fun getCurrentLocationForForm() {
        viewModelScope.launch {
            _formState.update { it.copy(isGeocoding = true, error = null) }
            try {
                // Check permissions (this should ideally be done in the UI layer before calling ViewModel)
                // For demonstration, assuming permissions are granted.
                // In a real app, you'd handle SecurityException here or upfront.
                val location = fusedLocationProviderClient.lastLocation.await()

                if (location != null) {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                    if (!addresses.isNullOrEmpty()) {
                        val address = addresses[0]
                        val fullAddress = address.getAddressLine(0) ?: "${location.latitude}, ${location.longitude}"
                        val region = address.adminArea ?: "" // State/Province
                        
                        _formState.update { currentState ->
                            currentState.copy(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                address = fullAddress,
                                region = region,
                                error = null
                            )
                        }
                    } else {
                        _formState.update { it.copy(error = "현재 위치의 주소를 찾을 수 없습니다.") }
                    }
                } else {
                    _formState.update { it.copy(error = "현재 위치를 가져올 수 없습니다.") }
                }
            } catch (e: SecurityException) {
                val errorMessage = "위치 권한이 필요합니다."
                Log.e("AddLocationViewModel", "Location permission error: $errorMessage", e)
                _formState.update { it.copy(error = errorMessage) }
            } catch (e: IOException) {
                val errorMessage = "위치 주소 변환 중 네트워크 오류가 발생했습니다."
                Log.e("AddLocationViewModel", "Reverse geocoding network error: $errorMessage", e)
                _formState.update { it.copy(error = errorMessage) }
            } catch (e: Exception) {
                val errorMessage = "현재 위치를 가져오는 중 오류가 발생했습니다."
                Log.e("AddLocationViewModel", "Get current location error: $errorMessage", e)
                _formState.update { it.copy(error = errorMessage) }
            } finally {
                _formState.update { it.copy(isGeocoding = false) }
            }
        }
    }
}