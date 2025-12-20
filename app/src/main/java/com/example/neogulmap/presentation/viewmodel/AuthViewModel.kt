package com.example.neogulmap.presentation.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.neogulmap.domain.repository.AuthRepository
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.isLoggedIn().collect {
                _isLoggedIn.value = it
            }
        }
    }

    fun loginWithKakao(context: Context) {
        _isLoading.value = true
        
        // 카카오톡 설치 여부 확인
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    Log.e("AuthViewModel", "카카오톡 로그인 실패", error)
                    // 카카오톡 설치되어 있지만 설정 문제 등으로 실패 시 계정으로 로그인 시도
                    loginWithKakaoAccount(context)
                } else if (token != null) {
                    handleKakaoToken(token)
                }
            }
        } else {
            loginWithKakaoAccount(context)
        }
    }

    private fun loginWithKakaoAccount(context: Context) {
        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
            if (error != null) {
                Log.e("AuthViewModel", "카카오 계정 로그인 실패", error)
                _isLoading.value = false
            } else if (token != null) {
                handleKakaoToken(token)
            }
        }
    }

    private fun handleKakaoToken(token: OAuthToken) {
        viewModelScope.launch {
            val result = authRepository.loginWithKakao(token.accessToken)
            if (result.isSuccess) {
                Log.d("AuthViewModel", "서버 로그인 성공")
            } else {
                Log.e("AuthViewModel", "서버 로그인 실패", result.exceptionOrNull())
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e("AuthViewModel", "카카오 로그아웃 실패", error)
                }
                viewModelScope.launch {
                    authRepository.logout()
                }
            }
        }
    }
}
