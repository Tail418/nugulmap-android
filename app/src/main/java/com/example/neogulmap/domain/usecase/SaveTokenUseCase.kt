package com.example.neogulmap.domain.usecase

import com.example.neogulmap.data.local.TokenRepository
import javax.inject.Inject

class SaveTokenUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    suspend operator fun invoke(accessToken: String, refreshToken: String) {
        tokenRepository.saveAccessToken(accessToken)
        tokenRepository.saveRefreshToken(refreshToken)
    }
}
