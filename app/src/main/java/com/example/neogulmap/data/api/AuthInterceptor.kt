package com.example.neogulmap.data.api

import com.example.neogulmap.data.local.TokenRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenRepository: TokenRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = runBlocking {
            tokenRepository.getAccessToken().first()
        }

        val request = chain.request().newBuilder()
            .apply {
                if (!accessToken.isNullOrBlank()) {
                    addHeader("Authorization", "Bearer $accessToken")
                }
            }
            .build()

        return chain.proceed(request)
    }
}
