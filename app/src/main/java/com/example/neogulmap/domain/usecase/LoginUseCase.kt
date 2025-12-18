package com.example.neogulmap.domain.usecase

import com.example.neogulmap.data.model.UserRequest
import com.example.neogulmap.domain.repository.UserRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    /**
     * Attempts to log in or register a user on the backend using their social login info.
     *
     * @param oauthId The unique ID from the OAuth provider.
     * @param email The user's email.
     * @param nickname The user's nickname.
     * @param provider The name of the OAuth provider (e.g., "kakao").
     * @return A Result wrapper indicating success or failure.
     */
    suspend operator fun invoke(
        oauthId: String,
        email: String,
        nickname: String,
        provider: String
    ): Result<Unit> {
        val userRequest = UserRequest(
            email = email,
            oauthId = oauthId,
            oauthProvider = provider,
            nickname = nickname
        )
        // For now, we don't handle profile images during the initial login.
        // This can be a separate feature.
        return userRepository.createUser(userRequest, null)
    }
}
