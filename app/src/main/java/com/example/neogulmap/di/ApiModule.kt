package com.example.neogulmap.di

import com.example.neogulmap.data.api.NugulApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun provideNugulApi(retrofit: Retrofit): NugulApi {
        return retrofit.create(NugulApi::class.java)
    }
}
