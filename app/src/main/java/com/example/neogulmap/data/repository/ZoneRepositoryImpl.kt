package com.example.neogulmap.data.repository

import android.content.Context
import android.net.Uri
import com.example.neogulmap.data.api.NugulApi
import com.example.neogulmap.data.local.ZoneDao
import com.example.neogulmap.data.local.toDomain
import com.example.neogulmap.data.local.toEntity
import com.example.neogulmap.data.model.ZoneDto
import com.example.neogulmap.domain.model.Zone
import com.example.neogulmap.domain.repository.ZoneRepository
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class ZoneRepositoryImpl @Inject constructor(
    private val api: NugulApi,
    private val zoneDao: ZoneDao,
    private val gson: Gson, // Inject Gson
    @ApplicationContext private val context: Context // Inject Context for URI handling
) : ZoneRepository {

    private fun mapToDomain(dto: ZoneDto): Zone {
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
        imageUri: Uri?
    ): Result<Zone> {
        return try {
            // Extract region from address (assuming standard Korean address format: "Province/City District ...")
            val region = address.split(" ").firstOrNull() ?: "Unknown"

            // Use a Map or a specific DTO without ID for creation to avoid sending 'id' field
            // Server ZoneRequest fields: region, type, subtype, description, latitude, longitude, size, address, user, image
            val zoneDataMap = mapOf(
                "region" to region,
                "type" to type,
                "subtype" to "실외", // Default to Outdoor if not specified
                "description" to name, // Map 'name' to 'description'
                "latitude" to latitude,
                "longitude" to longitude,
                "size" to "중형", // Default to Medium if not specified
                "address" to address,
                "user" to userId,
                "image" to null // Explicitly set image to null as it's handled via MultipartFile
            )

            val zoneDataJson = gson.toJson(zoneDataMap)
            val zoneDataRequestBody = zoneDataJson.toRequestBody("application/json".toMediaTypeOrNull())

            var imagePart: MultipartBody.Part? = null
            imageUri?.let { uri ->
                val inputStream = context.contentResolver.openInputStream(uri)
                inputStream?.let { stream ->
                    val fileContent = stream.readBytes()
                    val requestBody = fileContent.toRequestBody("image/*".toMediaTypeOrNull())
                    // Frontend uses 'image' as part name, assuming backend expects it
                    imagePart = MultipartBody.Part.createFormData("image", "zone_image.jpg", requestBody) 
                }
            }

            val apiResponse = api.createZone(zoneDataRequestBody, imagePart)

            if (apiResponse.success && apiResponse.data != null) {
                Result.success(mapToDomain(apiResponse.data))
            } else {
                Result.failure(Exception(apiResponse.message ?: "Failed to create zone: Unknown API error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
