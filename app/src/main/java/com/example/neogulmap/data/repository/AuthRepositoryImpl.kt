package com.example.neogulmap.data.repository

import com.example.neogulmap.data.api.NugulApi
import com.example.neogulmap.data.local.TokenManager
import com.example.neogulmap.data.model.AuthRequestDto
import com.example.neogulmap.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: NugulApi,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun loginWithKakao(kakaoAccessToken: String): Result<Unit> {
        return loginWithSocial(kakaoAccessToken, "kakao")
    }

    override suspend fun loginWithSocial(accessToken: String, provider: String): Result<Unit> {
        return try {
            val request = AuthRequestDto(accessToken, provider)
            val response = api.loginWithKakao(request)
            
            if (response.success && response.data != null) {
                tokenManager.saveToken(response.data.token, response.data.userId)
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenManager.clearToken()
    }

    override fun getAccessToken(): Flow<String?> = tokenManager.accessToken

    override fun getUserId(): Flow<Long?> = tokenManager.userId

    override fun isLoggedIn(): Flow<Boolean> = tokenManager.accessToken.map { it != null }
}
