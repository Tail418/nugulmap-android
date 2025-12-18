package com.example.neogulmap.data.model

data class UserRequest(
    val email: String,
    val nickname: String,
    val oauthId: String,
    val oauthProvider: String
)
