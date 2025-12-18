package com.example.neogulmap.data.model

import com.example.neogulmap.domain.model.Zone

data class ZoneDto(
    val id: Long,
    val region: String? = null,
    val type: String? = null, // Outdoor, Booth, etc.
    val subtype: String? = null,
    val description: String? = null,
    val latitude: Double,
    val longitude: Double,
    val size: String? = null,
    val address: String? = null,
    val user: String? = null, // UserId or Name
    val image: String? = null, // Filename
    
    // Compatibility fields for DevB (optional)
    val name: String? = null,
    val imageUrl: String? = null
)

/**
 * Maps a ZoneDto (data layer) to a Zone (domain layer) object.
 */
fun ZoneDto.toDomain(): Zone {
    return Zone(
        id = this.id,
        region = this.region ?: "Unknown Region",
        type = this.type ?: "Unknown Type",
        subtype = this.subtype,
        description = this.description,
        latitude = this.latitude,
        longitude = this.longitude,
        size = this.size,
        address = this.address,
        user = this.user,
        image = this.image
    )
}