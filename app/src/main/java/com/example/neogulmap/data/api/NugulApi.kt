package com.example.neogulmap.data.api

import com.example.neogulmap.data.model.ApiResponse
import com.example.neogulmap.data.model.ZoneDto
import retrofit2.http.GET
import retrofit2.http.Query

import com.example.neogulmap.data.model.ZoneListResponse

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface NugulApi {

    @GET("zones")
    suspend fun getAllZonesList(): ApiResponse<ZoneListResponse>

    @GET("zones/paged")
    suspend fun getZonesPaged(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ApiResponse<PagedZoneData>

    @GET("oauth2/login-urls")
    suspend fun getLoginUrls(): ApiResponse<LoginUrlsData>

    @Multipart
    @POST("users")
    suspend fun createUser(
        @Part("userData") userData: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): ApiResponse<Unit> // Assuming Unit or UserDto return
}

data class PagedZoneData(
    val content: List<ZoneDto>,
    val pageNumber: Int,
    val pageSize: Int,
    val totalElements: Long
)

data class LoginUrlsData(
    val loginUrls: Map<String, String>
)
