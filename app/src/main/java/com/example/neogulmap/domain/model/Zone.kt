package com.example.neogulmap.domain.model

data class Zone(
    val id: Long,
    val region: String,
    val type: String,
    val subtype: String?,
    val description: String?,
    val latitude: Double,
    val longitude: Double,
    val size: String?,
    val address: String?,
    val user: String?,
    val image: String?
)
