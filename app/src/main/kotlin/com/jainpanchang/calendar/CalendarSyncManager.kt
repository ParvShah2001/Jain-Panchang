package com.jainpanchang.calendar

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import com.jainpanchang.engine.model.Festival
import java.time.LocalDate
import java.time.ZoneId
import java.util.TimeZone

object CalendarSyncManager {

    fun syncFestivalToCalendar(
        context: Context,
        festival: Festival,
        date: LocalDate
    ): Uri? {
        val cr = context.contentResolver

        val startMillis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val values = ContentValues().apply {
            put(CalendarContract.Events.DTSTART, startMillis)
            put(CalendarContract.Events.DTEND, endMillis)
            put(CalendarContract.Events.TITLE, "${festival.nameGu} (${festival.nameEn})")
            put(CalendarContract.Events.DESCRIPTION, festival.description)
            put(CalendarContract.Events.CALENDAR_ID, 1) // Primary user calendar
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            put(CalendarContract.Events.ALL_DAY, 1)
        }

        return try {
            cr.insert(CalendarContract.Events.CONTENT_URI, values)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
