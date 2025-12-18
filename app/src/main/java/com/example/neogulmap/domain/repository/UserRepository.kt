package com.example.neogulmap.domain.repository

import com.example.neogulmap.data.api.UserRequest
import java.io.File

interface UserRepository {
    /**
     * Creates a user on the backend, typically after a successful social login.
     *
     * @param userRequest The user data obtained from the social login provider.
     * @param profileImage An optional profile image file.
     * @return A Result wrapper indicating success or failure.
     */
    suspend fun createUser(userRequest: UserRequest, profileImage: File?): Result<Unit>
}
