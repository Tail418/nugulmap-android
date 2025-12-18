package com.example.neogulmap.data.repository

import com.example.neogulmap.data.api.NugulApi
import com.example.neogulmap.data.model.UserRequest
import com.example.neogulmap.domain.repository.UserRepository
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: NugulApi,
    private val gson: Gson
) : UserRepository {

    override suspend fun createUser(userRequest: UserRequest, profileImage: File?): Result<Unit> {
        return try {
            val json = gson.toJson(userRequest)
            val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())

            val imagePart = profileImage?.let {
                val fileBody = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("profileImage", it.name, fileBody)
            }

            val response = api.createUser(requestBody, imagePart)
            if (response.success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.message ?: "Failed to create user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
