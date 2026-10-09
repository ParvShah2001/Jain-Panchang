package com.jainpanchang.engine.astro

import com.jainpanchang.engine.model.ChoghadiyaSlot
import com.jainpanchang.engine.model.ChoghadiyaType
import com.jainpanchang.engine.model.Vara
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object ChoghadiyaCalculator {

    // Day sequences for each day of the week (Sun=Ravivara .. Sat=Shanivara)
    private val DAY_SEQUENCES = mapOf(
        Vara.RAVIVARA to listOf(
            ChoghadiyaType.UDVEG, ChoghadiyaType.CHAR, ChoghadiyaType.LABH, ChoghadiyaType.AMRIT,
            ChoghadiyaType.KAAL, ChoghadiyaType.SHUBH, ChoghadiyaType.ROG, ChoghadiyaType.UDVEG
        ),
        Vara.SOMAVARA to listOf(
            ChoghadiyaType.AMRIT, ChoghadiyaType.KAAL, ChoghadiyaType.SHUBH, ChoghadiyaType.ROG,
            ChoghadiyaType.UDVEG, ChoghadiyaType.CHAR, ChoghadiyaType.LABH, ChoghadiyaType.AMRIT
        ),
        Vara.MANGALAVARA to listOf(
            ChoghadiyaType.ROG, ChoghadiyaType.UDVEG, ChoghadiyaType.CHAR, ChoghadiyaType.LABH,
            ChoghadiyaType.AMRIT, ChoghadiyaType.KAAL, ChoghadiyaType.SHUBH, ChoghadiyaType.ROG
        ),
        Vara.BUDHAVARA to listOf(
            ChoghadiyaType.LABH, ChoghadiyaType.AMRIT, ChoghadiyaType.KAAL, ChoghadiyaType.SHUBH,
            ChoghadiyaType.ROG, ChoghadiyaType.UDVEG, ChoghadiyaType.CHAR, ChoghadiyaType.LABH
        ),
        Vara.GURUVARA to listOf(
            ChoghadiyaType.SHUBH, ChoghadiyaType.ROG, ChoghadiyaType.UDVEG, ChoghadiyaType.CHAR,
            ChoghadiyaType.LABH, ChoghadiyaType.AMRIT, ChoghadiyaType.KAAL, ChoghadiyaType.SHUBH
        ),
        Vara.SHUKRAVARA to listOf(
            ChoghadiyaType.CHAR, ChoghadiyaType.LABH, ChoghadiyaType.AMRIT, ChoghadiyaType.KAAL,
            ChoghadiyaType.SHUBH, ChoghadiyaType.ROG, ChoghadiyaType.UDVEG, ChoghadiyaType.CHAR
        ),
        Vara.SHANIVARA to listOf(
            ChoghadiyaType.KAAL, ChoghadiyaType.SHUBH, ChoghadiyaType.ROG, ChoghadiyaType.UDVEG,
            ChoghadiyaType.CHAR, ChoghadiyaType.LABH, ChoghadiyaType.AMRIT, ChoghadiyaType.KAAL
        )
    )

    // Night sequences
    private val NIGHT_SEQUENCES = mapOf(
        Vara.RAVIVARA to listOf(
            ChoghadiyaType.SHUBH, ChoghadiyaType.AMRIT, ChoghadiyaType.CHAR, ChoghadiyaType.ROG,
            ChoghadiyaType.KAAL, ChoghadiyaType.LABH, ChoghadiyaType.UDVEG, ChoghadiyaType.SHUBH
        ),
        Vara.SOMAVARA to listOf(
            ChoghadiyaType.CHAR, ChoghadiyaType.ROG, ChoghadiyaType.KAAL, ChoghadiyaType.LABH,
            ChoghadiyaType.UDVEG, ChoghadiyaType.SHUBH, ChoghadiyaType.AMRIT, ChoghadiyaType.CHAR
        ),
        Vara.MANGALAVARA to listOf(
            ChoghadiyaType.KAAL, ChoghadiyaType.LABH, ChoghadiyaType.UDVEG, ChoghadiyaType.SHUBH,
            ChoghadiyaType.AMRIT, ChoghadiyaType.CHAR, ChoghadiyaType.ROG, ChoghadiyaType.KAAL
        ),
        Vara.BUDHAVARA to listOf(
            ChoghadiyaType.UDVEG, ChoghadiyaType.SHUBH, ChoghadiyaType.AMRIT, ChoghadiyaType.CHAR,
            ChoghadiyaType.ROG, ChoghadiyaType.KAAL, ChoghadiyaType.LABH, ChoghadiyaType.UDVEG
        ),
        Vara.GURUVARA to listOf(
            ChoghadiyaType.AMRIT, ChoghadiyaType.CHAR, ChoghadiyaType.ROG, ChoghadiyaType.KAAL,
            ChoghadiyaType.LABH, ChoghadiyaType.UDVEG, ChoghadiyaType.SHUBH, ChoghadiyaType.AMRIT
        ),
        Vara.SHUKRAVARA to listOf(
            ChoghadiyaType.ROG, ChoghadiyaType.KAAL, ChoghadiyaType.LABH, ChoghadiyaType.UDVEG,
            ChoghadiyaType.SHUBH, ChoghadiyaType.AMRIT, ChoghadiyaType.CHAR, ChoghadiyaType.ROG
        ),
        Vara.SHANIVARA to listOf(
            ChoghadiyaType.LABH, ChoghadiyaType.UDVEG, ChoghadiyaType.SHUBH, ChoghadiyaType.AMRIT,
            ChoghadiyaType.CHAR, ChoghadiyaType.ROG, ChoghadiyaType.KAAL, ChoghadiyaType.LABH
        )
    )

    fun calculateDayChoghadiya(
        sunrise: ZonedDateTime,
        sunset: ZonedDateTime,
        vara: Vara
    ): List<ChoghadiyaSlot> {
        val totalMillis = Duration.between(sunrise, sunset).toMillis()
        val partMillis = totalMillis / 8.0
        val sequence = DAY_SEQUENCES[vara] ?: DAY_SEQUENCES.values.first()
        val fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        return sequence.mapIndexed { idx, type ->
            val start = sunrise.plusSeconds(((idx * partMillis) / 1000.0).toLong())
            val end = if (idx == 7) sunset else sunrise.plusSeconds((((idx + 1) * partMillis) / 1000.0).toLong())
            ChoghadiyaSlot(
                index = idx + 1,
                isDay = true,
                type = type,
                startIso = start.format(fmt),
                endIso = end.format(fmt)
            )
        }
    }

    fun calculateNightChoghadiya(
        sunset: ZonedDateTime,
        nextSunrise: ZonedDateTime,
        vara: Vara
    ): List<ChoghadiyaSlot> {
        val totalMillis = Duration.between(sunset, nextSunrise).toMillis()
        val partMillis = totalMillis / 8.0
        val sequence = NIGHT_SEQUENCES[vara] ?: NIGHT_SEQUENCES.values.first()
        val fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        return sequence.mapIndexed { idx, type ->
            val start = sunset.plusSeconds(((idx * partMillis) / 1000.0).toLong())
            val end = if (idx == 7) nextSunrise else sunset.plusSeconds((((idx + 1) * partMillis) / 1000.0).toLong())
            ChoghadiyaSlot(
                index = idx + 1,
                isDay = false,
                type = type,
                startIso = start.format(fmt),
                endIso = end.format(fmt)
            )
        }
    }
}
