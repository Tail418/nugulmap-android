package com.example.neogulmap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed interface ReportUiState {
    object Loading : ReportUiState
    object Success : ReportUiState // Placeholder for success state
    data class Error(val message: String) : ReportUiState
}

@HiltViewModel
class ReportViewModel @Inject constructor(
    // Dependencies will be injected here later if needed
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReportUiState>(ReportUiState.Loading)
    val uiState = _uiState.asStateFlow()

    // Functions for handling reporting logic will be added here
}
