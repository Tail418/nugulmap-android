package com.example.neogulmap.domain.model

/**
 * Domain layer representation of a smoking zone.
 * This is the object used in the UI and UseCases.
 */
data class Zone(
    val id: Long,
    val name: String, // A displayable name, maybe combining region and description
    val description: String?,
    val latitude: Double,
    val longitude: Double,
    val imageUrl: String?, // Full URL to the image
    val address: String?,
    val type: String, // e.g., Outdoor, Booth
    val size: String?
)

