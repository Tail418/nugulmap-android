package com.example.neogulmap.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ReportUiState(
    val address: String = "",
    val description: String = "",
    val imageUri: Uri? = null,
    val isLoading: Boolean = false,
    val canSubmit: Boolean = false,
    val error: String? = null,
    val reportSuccess: Boolean = false
)

@HiltViewModel
class ReportViewModel @Inject constructor(
    // private val submitReportUseCase: SubmitReportUseCase // To be added later
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState = _uiState.asStateFlow()

    fun onAddressChanged(address: String) {
        _uiState.update { currentState ->
            currentState.copy(
                address = address,
                canSubmit = address.isNotBlank() && currentState.description.isNotBlank()
            )
        }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { currentState ->
            currentState.copy(
                description = description,
                canSubmit = currentState.address.isNotBlank() && description.isNotBlank()
            )
        }
    }

    fun onImageSelected(uri: Uri?) {
        _uiState.update { it.copy(imageUri = uri) }
    }

    fun submitReport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Simulate network call
            kotlinx.coroutines.delay(2000)
            
            // In a real scenario, you would call the use case:
            // submitReportUseCase(
            //     address = uiState.value.address,
            //     description = uiState.value.description,
            //     imageUri = uiState.value.imageUri
            // ).onSuccess {
            //     _uiState.update { it.copy(isLoading = false, reportSuccess = true) }
            // }.onFailure { error ->
            //     _uiState.update { it.copy(isLoading = false, error = error.message) }
            // }
            
            // Placeholder logic:
            _uiState.update { it.copy(isLoading = false, reportSuccess = true) }
        }
    }
}
