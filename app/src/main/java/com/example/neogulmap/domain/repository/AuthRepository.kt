package com.example.neogulmap.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun loginWithKakao(kakaoAccessToken: String): Result<Unit>
    suspend fun loginWithSocial(accessToken: String, provider: String): Result<Unit>
    suspend fun logout()
    fun getAccessToken(): Flow<String?>
    fun isLoggedIn(): Flow<Boolean>
    fun getUserId(): Flow<Long?>
}
