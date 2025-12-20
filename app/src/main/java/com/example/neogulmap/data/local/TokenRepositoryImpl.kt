package com.example.neogulmap.data.local

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val tokenManager: TokenManager
) : TokenRepository {

    override suspend fun saveAccessToken(token: String) {
        // userId는 0으로 기본값 (추후 필요시 확장)
        tokenManager.saveToken(token, 0)
    }

    override suspend fun saveRefreshToken(token: String) {
        // Refresh token logic not fully implemented in TokenManager yet
    }

    override fun getAccessToken(): Flow<String?> = tokenManager.accessToken

    override fun getRefreshToken(): Flow<String?> = tokenManager.accessToken // Temporarily using same flow
}
