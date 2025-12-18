package com.example.neogulmap.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ProfileUiState(
    val nickname: String = "기본 닉네임",
    val profileImageUri: Uri? = null,
    val registeredPlaces: List<String> = listOf("Place 1", "Place 2", "Place 3"), // Dummy data
    val isProfileChanged: Boolean = false
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun onNicknameChanged(newNickname: String) {
        _uiState.update { currentState ->
            currentState.copy(
                nickname = newNickname,
                isProfileChanged = newNickname != currentState.nickname || currentState.profileImageUri != _uiState.value.profileImageUri // Simplified comparison
            )
        }
    }

    fun onProfileImageSelected(uri: Uri?) {
        _uiState.update { currentState ->
            currentState.copy(
                profileImageUri = uri,
                isProfileChanged = currentState.nickname != _uiState.value.nickname || uri != _uiState.value.profileImageUri
            )
        }
    }

    fun saveProfile() {
        // TODO: Implement actual save logic (e.g., update user profile in backend)
        _uiState.update { it.copy(isProfileChanged = false) } // Reset changed state after saving
        println("Saving profile: Nickname = ${uiState.value.nickname}, Image URI = ${uiState.value.profileImageUri}")
    }
}
