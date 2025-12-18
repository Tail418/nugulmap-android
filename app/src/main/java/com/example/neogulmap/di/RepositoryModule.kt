package com.example.neogulmap.di

import com.example.neogulmap.data.repository.ZoneRepositoryImpl
import com.example.neogulmap.domain.repository.ZoneRepository
import com.example.neogulmap.data.local.TokenRepository
import com.example.neogulmap.data.local.TokenRepositoryImpl
import com.example.neogulmap.data.repository.UserRepositoryImpl
import com.example.neogulmap.domain.repository.UserRepository
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
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindTokenRepository(
        tokenRepositoryImpl: TokenRepositoryImpl
    ): TokenRepository
}

