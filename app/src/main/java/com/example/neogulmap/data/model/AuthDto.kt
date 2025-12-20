package com.example.neogulmap.data.model

import com.google.gson.annotations.SerializedName

data class AuthRequestDto(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("provider") val provider: String
)

data class AuthResponseDto(
    @SerializedName("token") val token: String,
    @SerializedName("userId") val userId: Long,
    @SerializedName("email") val email: String?,
    @SerializedName("nickname") val nickname: String?,
    @SerializedName("profileImage") val profileImage: String?
)
