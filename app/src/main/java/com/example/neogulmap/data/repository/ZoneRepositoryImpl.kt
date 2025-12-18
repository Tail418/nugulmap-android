package com.example.neogulmap.data.repository

import com.example.neogulmap.data.api.NugulApi
import com.example.neogulmap.domain.model.Zone
import com.example.neogulmap.domain.repository.ZoneRepository
import java.io.File
import javax.inject.Inject

class ZoneRepositoryImpl @Inject constructor(
    private val api: NugulApi
) : ZoneRepository {

    // Helper to map DTO to Domain
    private fun mapToDomain(dto: com.example.neogulmap.data.model.ZoneDto): Zone {
        return Zone(
            id = dto.id,
            region = dto.region ?: "",
            type = dto.type ?: "",
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

    override suspend fun getZonesByRadius(latitude: Double, longitude: Double, radius: Int): Result<List<Zone>> {
        return try {
            val response = api.getAllZonesList()
            if (response.success && response.data != null) {
                val list = response.data.zones 
                    ?: response.data.content 
                    ?: response.data.zoneList
                    ?: response.data.list
                    ?: response.data.items
                    ?: response.data.data
                    ?: response.data.result
                    ?: emptyList()
                
                val domainList = list.map { mapToDomain(it) }
                // TODO: Filter by radius here if needed, or assume server handles it (currently getting all)
                Result.success(domainList)
            } else {
                Result.failure(Exception(response.message ?: "Unknown server error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createZone(
        latitude: Double,
        longitude: Double,
        name: String,
        address: String,
        type: String,
        userId: String,
        imageFile: File?
    ): Result<Zone> {
        // Not implemented yet - returning a failure or dummy
        return Result.failure(Exception("Create Zone not implemented yet"))
    }
}