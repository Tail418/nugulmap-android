package com.example.neogulmap.domain.usecase

import com.example.neogulmap.domain.model.Zone
import com.example.neogulmap.domain.repository.ZoneRepository
import javax.inject.Inject

class GetZonesUseCase @Inject constructor(
    private val repository: ZoneRepository
) {
    suspend operator fun invoke(latitude: Double = 0.0, longitude: Double = 0.0, radius: Int = 1000): Result<List<Zone>> {
        return repository.getZonesByRadius(latitude, longitude, radius)
    }
}