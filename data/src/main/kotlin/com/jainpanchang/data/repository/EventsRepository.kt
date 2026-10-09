package com.jainpanchang.data.repository

import com.jainpanchang.data.db.AppDatabase
import com.jainpanchang.data.db.MyEventEntity
import com.jainpanchang.engine.model.GeoLocation
import com.jainpanchang.engine.model.JainMonth
import com.jainpanchang.engine.model.Paksha
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

data class ComputedEventDate(
    val event: MyEventEntity,
    val gregorianDate: LocalDate
)

class EventsRepository(
    private val db: AppDatabase,
    private val panchangRepository: PanchangRepository
) {
    fun getAllEvents(): Flow<List<MyEventEntity>> = db.myEventDao().getAllEvents()

    suspend fun addEvent(event: MyEventEntity): Long = db.myEventDao().insertEvent(event)
    suspend fun updateEvent(event: MyEventEntity) = db.myEventDao().updateEvent(event)
    suspend fun deleteEvent(event: MyEventEntity) = db.myEventDao().deleteEvent(event)

    /**
     * Finds the exact Gregorian date for a tithi event in a target Gregorian year.
     */
    fun computeEventDateForYear(
        event: MyEventEntity,
        targetYear: Int,
        location: GeoLocation
    ): LocalDate {
        if (!event.isTithiBased) {
            return LocalDate.of(targetYear, event.gregorianMonth, event.gregorianDay)
        }

        val targetMonth = try { JainMonth.valueOf(event.jainMonth) } catch (e: Exception) { JainMonth.KARTIKA }
        val targetPaksha = try { Paksha.valueOf(event.paksha) } catch (e: Exception) { Paksha.SHUKLA }

        // Scan all 12 Gregorian months in the target year to find the day matching this tithi and month
        for (m in 1..12) {
            val days = panchangRepository.getMonthPanchang(targetYear, m, location)
            val match = days.firstOrNull {
                it.jainMonth == targetMonth &&
                it.tithi.paksha == targetPaksha &&
                it.tithi.number == event.tithi
            }
            if (match != null) {
                return LocalDate.parse(match.dateIso)
            }
        }

        // Fallback if not found in current year cycle
        return LocalDate.of(targetYear, 1, 1)
    }
}
