package com.jainpanchang.data.repository

import com.jainpanchang.data.assets.AssetDataLoader
import com.jainpanchang.data.db.AppDatabase
import com.jainpanchang.data.db.SavedCityEntity
import com.jainpanchang.data.schema.CityFileEntry
import com.jainpanchang.engine.model.GeoLocation
import kotlinx.coroutines.flow.Flow

class CityRepository(
    private val assetDataLoader: AssetDataLoader,
    private val db: AppDatabase
) {
    private var cachedOfflineCities: List<CityFileEntry>? = null

    fun getOfflineCities(): List<CityFileEntry> {
        if (cachedOfflineCities == null) {
            cachedOfflineCities = assetDataLoader.loadCities()
        }
        return cachedOfflineCities ?: emptyList()
    }

    fun searchCities(query: String): List<CityFileEntry> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return getOfflineCities()
        return getOfflineCities().filter {
            it.name.lowercase().contains(q) ||
            it.state.lowercase().contains(q) ||
            it.country.lowercase().contains(q)
        }
    }

    fun getSavedCities(): Flow<List<SavedCityEntity>> {
        return db.savedCityDao().getAllSavedCities()
    }

    suspend fun saveCity(city: GeoLocation, state: String = "", country: String = "") {
        db.savedCityDao().insertCity(
            SavedCityEntity(
                name = city.name,
                state = state,
                country = country.ifEmpty { "India" },
                latitude = city.latitude,
                longitude = city.longitude,
                timezoneId = city.timezoneId,
                isSelected = false
            )
        )
    }

    suspend fun deleteCity(entity: SavedCityEntity) {
        db.savedCityDao().deleteCity(entity)
    }
}
