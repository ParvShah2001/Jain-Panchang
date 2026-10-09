package com.jainpanchang.data.repository

import com.jainpanchang.data.assets.AssetDataLoader
import com.jainpanchang.engine.PanchangEngine
import com.jainpanchang.engine.model.*
import java.time.LocalDate

class PanchangRepository(
    private val panchangEngine: PanchangEngine,
    private val assetDataLoader: AssetDataLoader
) {
    private var cachedKalyanaks: List<Kalyanak>? = null
    private var cachedFestivals: List<Festival>? = null

    fun getKalyanaks(): List<Kalyanak> {
        if (cachedKalyanaks == null) {
            cachedKalyanaks = assetDataLoader.loadKalyanaks()
        }
        return cachedKalyanaks ?: emptyList()
    }

    fun getFestivals(): List<Festival> {
        if (cachedFestivals == null) {
            cachedFestivals = assetDataLoader.loadFestivals()
        }
        return cachedFestivals ?: emptyList()
    }

    fun getDailyPanchang(
        date: LocalDate,
        location: GeoLocation,
        ayanamsha: Ayanamsha = Ayanamsha.LAHIRI
    ): DailyPanchang {
        return panchangEngine.calculateDailyPanchang(
            date = date,
            location = location,
            ayanamsha = ayanamsha,
            kalyanaksData = getKalyanaks(),
            festivalsData = getFestivals()
        )
    }

    fun getMonthPanchang(
        year: Int,
        month: Int,
        location: GeoLocation,
        ayanamsha: Ayanamsha = Ayanamsha.LAHIRI
    ): List<DailyPanchang> {
        val firstDay = LocalDate.of(year, month, 1)
        val daysInMonth = firstDay.lengthOfMonth()
        val list = mutableListOf<DailyPanchang>()

        for (day in 1..daysInMonth) {
            val date = LocalDate.of(year, month, day)
            list.add(getDailyPanchang(date, location, ayanamsha))
        }
        return list
    }
}
