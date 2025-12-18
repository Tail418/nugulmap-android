package com.example.neogulmap.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SignupUiState(
    val nickname: String = "",
    val profileImageUri: Uri? = null,
    val canCompleteSignup: Boolean = false
)

class SignupViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState = _uiState.asStateFlow()

    fun onNicknameChanged(newNickname: String) {
        _uiState.update { currentState ->
            currentState.copy(
                nickname = newNickname,
                canCompleteSignup = newNickname.isNotBlank()
            )
        }
    }

    fun onProfileImageSelected(uri: Uri?) {
        _uiState.update { currentState ->
            currentState.copy(profileImageUri = uri)
        }
    }

    fun completeSignup() {
        // TODO: Implement actual signup logic, e.g., send nickname and profile image to backend
        // For now, it just simulates completion
        _uiState.update { it.copy(canCompleteSignup = false) } // Disable button after action
    }
}