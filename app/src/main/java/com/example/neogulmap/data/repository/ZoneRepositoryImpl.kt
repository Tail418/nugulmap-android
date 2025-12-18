package com.example.neogulmap.data.repository

import com.example.neogulmap.data.api.NugulApi
import com.example.neogulmap.domain.model.Zone
import com.example.neogulmap.domain.repository.ZoneRepository
import javax.inject.Inject

class ZoneRepositoryImpl @Inject constructor(
    private val api: NugulApi
) : ZoneRepository {
    override suspend fun getZones(): List<Zone> {
        val response = api.getAllZonesList()
        if (response.success && response.data != null) {
            // Try to find the list in known fields
            android.util.Log.d("ZoneRepo", "Response Data: ${response.data}")
            val list = response.data.zones 
                ?: response.data.content 
                ?: response.data.zoneList
                ?: response.data.list
                ?: response.data.items
                ?: response.data.data
                ?: response.data.result
                ?: emptyList()
            
            android.util.Log.d("ZoneRepo", "Parsed list size: ${list.size}")
            if (response.data.zones == null) android.util.Log.d("ZoneRepo", "response.data.zones is NULL")
            else android.util.Log.d("ZoneRepo", "response.data.zones size: ${response.data.zones.size}")
            
            return list.map { dto ->
                Zone(
                    id = dto.id,
                    region = dto.region,
                    type = dto.type,
                    subtype = dto.subtype,
                    description = dto.description,
                    latitude = dto.latitude,
                    longitude = dto.longitude,
                    size = dto.size,
                    address = dto.address,
                    user = dto.user,
                    image = dto.image
                )
            }
        } else {
            // Handle logical error from server (e.g. success=false)
            // For now, return empty or throw exception
            throw Exception(response.message ?: "Unknown server error")
        }
    }
}
