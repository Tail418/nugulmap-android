package com.example.neogulmap.data.repository

import com.example.neogulmap.data.api.NugulApi
import com.example.neogulmap.data.api.ZoneRequest
import com.example.neogulmap.data.model.toDomain
import com.example.neogulmap.domain.model.Zone
import com.example.neogulmap.domain.repository.ZoneRepository
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class ZoneRepositoryImpl @Inject constructor(
    private val api: NugulApi,
    private val gson: Gson
) : ZoneRepository {

    override suspend fun getZonesByRadius(
        latitude: Double,
        longitude: Double,
        radius: Int
    ): Result<List<Zone>> {
        return try {
            val response = api.getZonesByRadius(latitude, longitude, radius)
            if (response.success && response.data != null) {
                Result.success(response.data.map { it.toDomain() })
            } else {
                Result.failure(Exception(response.message ?: "Unknown error"))
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
        return try {
            val zoneRequest = ZoneRequest(
                latitude = latitude,
                longitude = longitude,
                description = name,
                address = address,
                type = type,
                user = userId,
                // Hardcoded region for now, should be dynamic
                region = "Default Region"
            )
            val json = gson.toJson(zoneRequest)
            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

            val imagePart = imageFile?.let {
                val fileBody = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", it.name, fileBody)
            }

            val response = api.createZone(requestBody, imagePart)
            if (response.success && response.data != null) {
                Result.success(response.data.toDomain())
            } else {
                Result.failure(Exception(response.message ?: "Failed to create zone"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
