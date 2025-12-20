package com.example.neogulmap.di

import com.example.neogulmap.data.repository.ZoneRepositoryImpl
import com.example.neogulmap.domain.repository.ZoneRepository
import com.example.neogulmap.data.repository.AuthRepositoryImpl
import com.example.neogulmap.domain.repository.AuthRepository
import com.example.neogulmap.data.local.TokenRepository
import com.example.neogulmap.data.local.TokenRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindZoneRepository(
        zoneRepositoryImpl: ZoneRepositoryImpl
    ): ZoneRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTokenRepository(
        tokenRepositoryImpl: TokenRepositoryImpl
    ): TokenRepository
}

