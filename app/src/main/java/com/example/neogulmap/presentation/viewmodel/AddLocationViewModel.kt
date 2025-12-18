package com.example.neogulmap.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AddLocationUiState(
    val address: String = "",
    val description: String = "",
    val imageUri: Uri? = null,
    val zoneType: String = "",
    val zoneSize: String = "",
    val canSubmit: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSubmissionSuccess: Boolean = false
)

class AddLocationViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AddLocationUiState())
    val uiState = _uiState.asStateFlow()

    private fun updateCanSubmit(currentState: AddLocationUiState): Boolean {
        return currentState.address.isNotBlank() && currentState.description.isNotBlank() && !currentState.isLoading
    }

    fun onAddressChanged(newAddress: String) {
        _uiState.update { currentState ->
            val newState = currentState.copy(address = newAddress)
            newState.copy(canSubmit = updateCanSubmit(newState))
        }
    }

    fun onDescriptionChanged(newDescription: String) {
        _uiState.update { currentState ->
            val newState = currentState.copy(description = newDescription)
            newState.copy(canSubmit = updateCanSubmit(newState))
        }
    }

    fun onImageSelected(uri: Uri?) {
        _uiState.update { it.copy(imageUri = uri) }
    }

    fun onZoneTypeChanged(newType: String) {
        _uiState.update { it.copy(zoneType = newType) }
    }

    fun onZoneSizeChanged(newSize: String) {
        _uiState.update { it.copy(zoneSize = newSize) }
    }

    fun submitLocation() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        // TODO: Implement actual submission logic to backend
        // For now, simulate success
        _uiState.update {
            it.copy(
                isLoading = false,
                isSubmissionSuccess = true,
                address = "",
                description = "",
                imageUri = null,
                zoneType = "",
                zoneSize = "",
                canSubmit = false
            )
        }
    }

    fun resetSubmissionStatus() {
        _uiState.update { it.copy(isSubmissionSuccess = false, error = null) }
    }
}
