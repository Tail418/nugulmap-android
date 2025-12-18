package com.example.neogulmap.domain.usecase

import com.example.neogulmap.domain.model.Zone
import com.example.neogulmap.domain.repository.ZoneRepository
import javax.inject.Inject

/**
 * A use case that fetches a list of smoking zones within a specific radius.
 */
class GetZonesUseCase @Inject constructor(
    private val repository: ZoneRepository
) {
    /**
     * @param latitude The center latitude of the search area.
     * @param longitude The center longitude of the search area.
     * @param radius The radius in meters (e.g., 500).
     * @return A Result wrapper containing the list of Zones on success, or an exception on failure.
     */
    suspend operator fun invoke(latitude: Double, longitude: Double, radius: Int): Result<List<Zone>> {
        // Basic validation can be added here if needed
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            return Result.failure(IllegalArgumentException("Invalid latitude or longitude"))
        }
        if (radius <= 0) {
            return Result.failure(IllegalArgumentException("Radius must be positive"))
        }
        return repository.getZonesByRadius(latitude, longitude, radius)
    }
}
