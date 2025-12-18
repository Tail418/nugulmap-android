package com.example.neogulmap.domain.repository

import com.example.neogulmap.domain.model.Zone
import java.io.File

interface ZoneRepository {
    /**
     * Fetches a list of smoking zones from the remote server within a specified radius.
     *
     * @param latitude The center latitude of the search area.
     * @param longitude The center longitude of the search area.
     * @param radius The radius in meters.
     * @return A Result wrapper containing the list of Zones on success, or an exception on failure.
     */
    suspend fun getZonesByRadius(latitude: Double, longitude: Double, radius: Int): Result<List<Zone>>

    /**
     * Creates a new smoking zone on the remote server.
     *
     * @param latitude The latitude of the new zone.
     * @param longitude The longitude of the new zone.
     * @param name The name or description of the zone.
     * @param imageFile An optional image file of the zone.
     * @return A Result wrapper containing the created Zone on success, or an exception on failure.
     */
    suspend fun createZone(
        latitude: Double,
        longitude: Double,
        name: String,
        address: String,
        type: String,
        userId: String,
        imageFile: File?
    ): Result<Zone>
}
