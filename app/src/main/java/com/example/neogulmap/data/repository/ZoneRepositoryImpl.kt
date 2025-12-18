package com.example.neogulmap.data.repository

import com.example.neogulmap.data.api.NugulApi
import com.example.neogulmap.data.local.ZoneDao
import com.example.neogulmap.data.local.toDomain
import com.example.neogulmap.data.local.toEntity
import com.example.neogulmap.domain.model.Zone
import com.example.neogulmap.domain.repository.ZoneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

class ZoneRepositoryImpl @Inject constructor(
    private val api: NugulApi,
    private val zoneDao: ZoneDao
) : ZoneRepository {

    private fun mapToDomain(dto: com.example.neogulmap.data.model.ZoneDto): Zone {
        return Zone(
            id = dto.id,
            region = dto.region ?: "Unknown Region",
            type = dto.type ?: "Unknown Type",
            subtype = dto.subtype,
            description = dto.description,
            latitude = dto.latitude,
            longitude = dto.longitude,
            size = dto.size,
            address = dto.address,
            user = dto.user,
            image = dto.image,
            name = dto.name,
            imageUrl = dto.imageUrl
        )
    }

    override suspend fun getZonesByRadius(latitude: Double, longitude: Double, radius: Int): Flow<Result<List<Zone>>> = channelFlow {
        // 1. Fetch from network in parallel
        launch(Dispatchers.IO) {
            try {
                val apiResponse = api.getAllZonesList()

                if (apiResponse.success && apiResponse.data != null) {
                    val dtoList = apiResponse.data.zones
                        ?: apiResponse.data.content
                        ?: apiResponse.data.zoneList
                        ?: apiResponse.data.list
                        ?: apiResponse.data.data
                        ?: apiResponse.data.result
                        ?: emptyList()

                    val domainList = dtoList.map { mapToDomain(it) }

                    // Update local DB
                    zoneDao.deleteAll()
                    zoneDao.insertAll(domainList.map { it.toEntity() })
                } else {
                    val errorMessage = apiResponse.message ?: "Unknown API error"
                    // Optionally send error if needed, but main data source is DB
                    // trySend(Result.failure(Exception(errorMessage))) 
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // trySend(Result.failure(e))
            }
        }

        // 2. Observe local DB (Source of Truth)
        zoneDao.getAllZones().collect { entities ->
            send(Result.success(entities.map { it.toDomain() }))
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