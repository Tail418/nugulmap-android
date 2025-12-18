package com.example.neogulmap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class TermsUiState(
    val agreeAll: Boolean = false,
    val agreeServiceTerms: Boolean = false,
    val agreePrivacyPolicy: Boolean = false,
    val agreeMarketing: Boolean = false,
    val canProceed: Boolean = false
)

class TermsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TermsUiState())
    val uiState = _uiState.asStateFlow()

    fun onAgreeAllChanged(agreed: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                agreeAll = agreed,
                agreeServiceTerms = agreed,
                agreePrivacyPolicy = agreed,
                agreeMarketing = agreed,
                canProceed = agreed // If agreeAll, then mandatory terms are also agreed
            )
        }
    }

    fun onServiceTermsChanged(agreed: Boolean) {
        _uiState.update { currentState ->
            val newState = currentState.copy(agreeServiceTerms = agreed)
            updateAgreeAllAndCanProceed(newState)
        }
    }

    fun onPrivacyPolicyChanged(agreed: Boolean) {
        _uiState.update { currentState ->
            val newState = currentState.copy(agreePrivacyPolicy = agreed)
            updateAgreeAllAndCanProceed(newState)
        }
    }

    fun onMarketingChanged(agreed: Boolean) {
        _uiState.update { currentState ->
            val newState = currentState.copy(agreeMarketing = agreed)
            updateAgreeAllAndCanProceed(newState)
        }
    }

    private fun updateAgreeAllAndCanProceed(currentState: TermsUiState): TermsUiState {
        val allAgreed = currentState.agreeServiceTerms && currentState.agreePrivacyPolicy && currentState.agreeMarketing
        val canProceed = currentState.agreeServiceTerms && currentState.agreePrivacyPolicy
        return currentState.copy(
            agreeAll = allAgreed,
            canProceed = canProceed
        )
    }
}
