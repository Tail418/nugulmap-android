package com.example.neogulmap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neogulmap.domain.repository.AuthRepository
import com.kakao.sdk.auth.model.OAuthToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    object Success : LoginUiState
    data class Error(val message: String) : LoginUiState
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun handleKakaoLoginWithToken(token: OAuthToken?, error: Throwable?) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            if (error != null) {
                _uiState.value = LoginUiState.Error(error.message ?: "카카오 로그인 실패.")
                return@launch
            }

            if (token != null) {
                val loginResult = authRepository.loginWithKakao(token.accessToken)

                loginResult.onSuccess {
                    _uiState.value = LoginUiState.Success
                }.onFailure { backendError ->
                    _uiState.value = LoginUiState.Error(backendError.message ?: "서버 로그인에 실패했습니다.")
                }
            } else {
                _uiState.value = LoginUiState.Error("카카오 토큰 획득 실패.")
            }
        }
    }

    // Naver, Google 등도 동일한 방식으로 수정 가능 (생략 또는 필요시 추가)
}

