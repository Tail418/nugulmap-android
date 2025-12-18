package com.example.neogulmap.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neogulmap.domain.usecase.LoginUseCase
import com.example.neogulmap.domain.usecase.SaveTokenUseCase
import com.kakao.sdk.user.model.User
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
    private val loginUseCase: LoginUseCase,
    private val saveTokenUseCase: SaveTokenUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun handleKakaoLoginResult(user: User?, error: Throwable?) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            if (error != null) {
                _uiState.value = LoginUiState.Error(error.message ?: "카카오 로그인 실패.")
                return@launch
            }

            if (user?.id != null) {
                val oauthId = user.id.toString()
                val nickname = user.kakaoAccount?.profile?.nickname ?: "Nugul User"
                val email = user.kakaoAccount?.email ?: ""

                val loginResult = loginUseCase(
                    oauthId = oauthId,
                    email = email,
                    nickname = nickname,
                    provider = "kakao"
                )

                loginResult.onSuccess {
                    val fakeAccessToken = "fake_access_token_from_backend"
                    val fakeRefreshToken = "fake_refresh_token_from_backend"
                    saveTokenUseCase(fakeAccessToken, fakeRefreshToken)
                    
                    _uiState.value = LoginUiState.Success
                    
                }.onFailure { backendError ->
                    _uiState.value = LoginUiState.Error(backendError.message ?: "알 수 없는 백엔드 에러가 발생했습니다.")
                }

            } else {
                _uiState.value = LoginUiState.Error("카카오 로그인 사용자 정보 획득 실패.")
            }
        }
    }

    fun handleNaverLoginResult(oauthId: String?, nickname: String?, email: String?, error: Throwable?) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            if (error != null) {
                _uiState.value = LoginUiState.Error(error.message ?: "네이버 로그인 실패.")
                return@launch
            }

            if (oauthId != null) {
                val loginResult = loginUseCase(
                    oauthId = oauthId,
                    email = email ?: "",
                    nickname = nickname ?: "Nugul User",
                    provider = "naver"
                )

                loginResult.onSuccess {
                    val fakeAccessToken = "fake_access_token_from_backend_naver"
                    val fakeRefreshToken = "fake_refresh_token_from_backend_naver"
                    saveTokenUseCase(fakeAccessToken, fakeRefreshToken)
                    _uiState.value = LoginUiState.Success
                }.onFailure { backendError ->
                    _uiState.value = LoginUiState.Error(backendError.message ?: "알 수 없는 백엔드 에러가 발생했습니다.")
                }
            } else {
                _uiState.value = LoginUiState.Error("네이버 로그인 사용자 정보 획득 실패.")
            }
        }
    }

    fun handleGoogleLoginResult(oauthId: String?, nickname: String?, email: String?, error: Throwable?) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            if (error != null) {
                _uiState.value = LoginUiState.Error(error.message ?: "구글 로그인 실패.")
                return@launch
            }

            if (oauthId != null) {
                val loginResult = loginUseCase(
                    oauthId = oauthId,
                    email = email ?: "",
                    nickname = nickname ?: "Nugul User",
                    provider = "google"
                )

                loginResult.onSuccess {
                    val fakeAccessToken = "fake_access_token_from_backend_google"
                    val fakeRefreshToken = "fake_refresh_token_from_backend_google"
                    saveTokenUseCase(fakeAccessToken, fakeRefreshToken)
                    _uiState.value = LoginUiState.Success
                }.onFailure { backendError ->
                    _uiState.value = LoginUiState.Error(backendError.message ?: "알 수 없는 백엔드 에러가 발생했습니다.")
                }
            } else {
                _uiState.value = LoginUiState.Error("구글 로그인 사용자 정보 획득 실패.")
            }
        }
    }
}

