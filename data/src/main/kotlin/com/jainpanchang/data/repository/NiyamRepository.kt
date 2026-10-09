package com.jainpanchang.data.repository

import com.jainpanchang.data.assets.AssetDataLoader
import com.jainpanchang.data.db.AppDatabase
import com.jainpanchang.data.db.NiyamRecordEntity
import com.jainpanchang.data.schema.ChaudahNiyamEntry
import com.jainpanchang.data.schema.NiyamCategoryEntry
import com.jainpanchang.data.schema.NiyamData
import com.jainpanchang.data.schema.NiyamSuggestionEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class NiyamRepository(
    private val assetDataLoader: AssetDataLoader,
    private val db: AppDatabase
) {
    private var cachedData: NiyamData? = null

    private fun getData(): NiyamData {
        if (cachedData == null) {
            cachedData = assetDataLoader.loadNiyamData()
        }
        return cachedData!!
    }

    fun getCategories(): List<NiyamCategoryEntry> = getData().categories
    fun getChaudahNiyams(): List<ChaudahNiyamEntry> = getData().chaudahNiyam
    fun getSuggestions(): List<NiyamSuggestionEntry> = getData().suggestions

    fun getDailySuggestion(date: LocalDate): NiyamSuggestionEntry {
        val list = getSuggestions()
        val index = (date.dayOfYear - 1) % list.size
        return list[index]
    }

    fun getRecordsForDate(dateIso: String): Flow<List<NiyamRecordEntity>> {
        return db.niyamRecordDao().getRecordsForDate(dateIso)
    }

    fun getRecentRecords(startDateIso: String): Flow<List<NiyamRecordEntity>> {
        return db.niyamRecordDao().getRecentRecords(startDateIso)
    }

    suspend fun toggleNiyamCompletion(dateIso: String, niyamId: Int, isCompleted: Boolean, customMaryada: String = "") {
        db.niyamRecordDao().insertOrUpdate(
            NiyamRecordEntity(
                dateIso = dateIso,
                niyamId = niyamId,
                completed = isCompleted,
                customMaryada = customMaryada
            )
        )
    }
}
