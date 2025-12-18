package com.example.neogulmap.data.model

import com.example.neogulmap.BuildConfig
import com.example.neogulmap.domain.model.Zone

data class ZoneDto(
    val id: Long,
    val region: String,
    val type: String, // Outdoor, Booth, etc.
    val subtype: String?,
    val description: String?,
    val latitude: Double,
    val longitude: Double,
    val size: String?,
    val address: String?,
    val user: String?, // UserId or Name
    val image: String? // Filename
)

/**
 * Maps a ZoneDto (data layer) to a Zone (domain layer) object.
 */
fun ZoneDto.toDomain(): Zone {
    return Zone(
        id = this.id,
        name = this.description ?: this.address ?: "흡연 구역",
        description = this.description,
        latitude = this.latitude,
        longitude = this.longitude,
        imageUrl = this.image?.let { "${BuildConfig.BASE_URL}/images/$it" },
        address = this.address,
        type = this.type,
        size = this.size
    )
}

