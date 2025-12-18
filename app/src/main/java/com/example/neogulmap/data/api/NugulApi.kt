package com.example.neogulmap.data.api

import com.example.neogulmap.data.model.ApiResponse
import com.example.neogulmap.data.model.ZoneDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

// Request Data Transfer Objects
data class UserRequest(
    val email: String,
    val oauthId: String,
    val oauthProvider: String,
    val nickname: String
)

data class ZoneRequest(
    val latitude: Double,
    val longitude: Double,
    val region: String,
    val type: String,
    val description: String,
    val address: String,
    val user: String // UserID or Name
)

interface NugulApi {

    /**
     * OAuth/Social login user creation.
     */
    @Multipart
    @POST("users")
    suspend fun createUser(
        @Part("userData") userRequest: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): ApiResponse<Unit> // Assuming the created user object is not immediately needed.

    /**
     * Search for smoking zones within a given radius.
     */
    @GET("zones")
    suspend fun getZonesByRadius(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radius: Int // in meters
    ): ApiResponse<List<ZoneDto>>

    /**
     * Create/Report a new smoking zone.
     */
    @Multipart
    @POST("zones")
    suspend fun createZone(
        @Part("data") zoneRequest: RequestBody,
        @Part image: MultipartBody.Part?
    ): ApiResponse<ZoneDto>

}
