package com.jainpanchang.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "saved_cities")
data class SavedCityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val state: String = "",
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneId: String,
    val isSelected: Boolean = false
)

@Dao
interface SavedCityDao {
    @Query("SELECT * FROM saved_cities ORDER BY name ASC")
    fun getAllSavedCities(): Flow<List<SavedCityEntity>>

    @Query("SELECT * FROM saved_cities WHERE isSelected = 1 LIMIT 1")
    fun getSelectedCity(): Flow<SavedCityEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCity(city: SavedCityEntity): Long

    @Query("UPDATE saved_cities SET isSelected = 0")
    suspend fun clearSelected(): Int

    @Query("UPDATE saved_cities SET isSelected = 1 WHERE id = :cityId")
    suspend fun setSelected(cityId: Long): Int

    @Delete
    suspend fun deleteCity(city: SavedCityEntity): Int
}

@Entity(tableName = "my_events")
data class MyEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val notes: String = "",
    val isTithiBased: Boolean = true,
    // Tithi fields
    val jainMonth: String = "KARTIKA",
    val paksha: String = "SHUKLA",
    val tithi: Int = 1,
    // Gregorian fallback
    val gregorianMonth: Int = 1,
    val gregorianDay: Int = 1,
    val reminderHour: Int = 8,
    val reminderMinute: Int = 0,
    val isReminderEnabled: Boolean = true
)

@Dao
interface MyEventDao {
    @Query("SELECT * FROM my_events ORDER BY id DESC")
    fun getAllEvents(): Flow<List<MyEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: MyEventEntity): Long

    @Update
    suspend fun updateEvent(event: MyEventEntity): Int

    @Delete
    suspend fun deleteEvent(event: MyEventEntity): Int
}

@Entity(
    tableName = "niyam_records",
    primaryKeys = ["dateIso", "niyamId"]
)
data class NiyamRecordEntity(
    val dateIso: String, // YYYY-MM-DD
    val niyamId: Int, // 1 to 14
    val completed: Boolean = false,
    val customMaryada: String = ""
)

@Dao
interface NiyamRecordDao {
    @Query("SELECT * FROM niyam_records WHERE dateIso = :dateIso")
    fun getRecordsForDate(dateIso: String): Flow<List<NiyamRecordEntity>>

    @Query("SELECT * FROM niyam_records WHERE dateIso >= :startDateIso ORDER BY dateIso ASC")
    fun getRecentRecords(startDateIso: String): Flow<List<NiyamRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: NiyamRecordEntity): Long
}
