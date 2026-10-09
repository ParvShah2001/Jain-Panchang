package com.jainpanchang.engine.astro

import com.jainpanchang.engine.model.HoraSlot
import com.jainpanchang.engine.model.Vara
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object HoraCalculator {

    // Chaldean descending order of planetary lords
    private val CHALDEAN_ORDER = listOf(
        Triple("Sun", "सूर्य", "સૂર્ય"),
        Triple("Venus", "शुक्र", "શુક્ર"),
        Triple("Mercury", "बुध", "બુધ"),
        Triple("Moon", "चंद्र", "ચંદ્ર"),
        Triple("Saturn", "शनि", "શનિ"),
        Triple("Jupiter", "गुरु", "ગુરુ"),
        Triple("Mars", "मंगल", "મંગળ")
    )

    private val VARA_START_INDEX = mapOf(
        Vara.RAVIVARA to 0, // Sun
        Vara.SOMAVARA to 3, // Moon
        Vara.MANGALAVARA to 6, // Mars
        Vara.BUDHAVARA to 2, // Mercury
        Vara.GURUVARA to 5, // Jupiter
        Vara.SHUKRAVARA to 1, // Venus
        Vara.SHANIVARA to 4  // Saturn
    )

    fun calculateHoras(
        sunrise: ZonedDateTime,
        sunset: ZonedDateTime,
        nextSunrise: ZonedDateTime,
        vara: Vara
    ): List<HoraSlot> {
        val startIdx = VARA_START_INDEX[vara] ?: 0
        val dayDuration = Duration.between(sunrise, sunset).toMillis() / 12.0
        val nightDuration = Duration.between(sunset, nextSunrise).toMillis() / 12.0
        val fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        val slots = mutableListOf<HoraSlot>()

        // 12 day Horas
        for (i in 0 until 12) {
            val lord = CHALDEAN_ORDER[(startIdx + i) % 7]
            val start = sunrise.plusSeconds(((i * dayDuration) / 1000.0).toLong())
            val end = if (i == 11) sunset else sunrise.plusSeconds((((i + 1) * dayDuration) / 1000.0).toLong())
            slots.add(
                HoraSlot(
                    hourIndex = i + 1,
                    planetName = lord.first,
                    nameHi = lord.second,
                    nameGu = lord.third,
                    startIso = start.format(fmt),
                    endIso = end.format(fmt)
                )
            )
        }

        // 12 night Horas
        for (i in 0 until 12) {
            val lord = CHALDEAN_ORDER[(startIdx + 12 + i) % 7]
            val start = sunset.plusSeconds(((i * nightDuration) / 1000.0).toLong())
            val end = if (i == 11) nextSunrise else sunset.plusSeconds((((i + 1) * nightDuration) / 1000.0).toLong())
            slots.add(
                HoraSlot(
                    hourIndex = 13 + i,
                    planetName = lord.first,
                    nameHi = lord.second,
                    nameGu = lord.third,
                    startIso = start.format(fmt),
                    endIso = end.format(fmt)
                )
            )
        }

        return slots
    }
}
