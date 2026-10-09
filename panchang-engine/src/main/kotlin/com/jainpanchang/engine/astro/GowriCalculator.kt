package com.jainpanchang.engine.astro

import com.jainpanchang.engine.model.GowriSlot
import com.jainpanchang.engine.model.Vara
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object GowriCalculator {

    data class GowriPeriodDef(val en: String, val hi: String, val gu: String, val isGood: Boolean)

    private val UTHI = GowriPeriodDef("Udhdhi", "उद्धि", "ઉદ્ધિ", true)
    private val AMIR = GowriPeriodDef("Amirdham", "अमृत", "અમૃત", true)
    private val ROGA = GowriPeriodDef("Rogam", "रोग", "રોગ", false)
    private val SORK = GowriPeriodDef("Shubham", "शुभ", "શુભ", true)
    private val BAND = GowriPeriodDef("Bandham", "बंधन", "બંધન", false)
    private val KALP = GowriPeriodDef("Labham", "लाभ", "લાભ", true)
    private val MARA = GowriPeriodDef("Maranam", "मरण", "મરણ", false)
    private val CHOR = GowriPeriodDef("Choram", "चोर", "ચોર", false)

    private val DAY_GOWRI = mapOf(
        Vara.RAVIVARA to listOf(UTHI, AMIR, ROGA, SORK, BAND, KALP, MARA, CHOR),
        Vara.SOMAVARA to listOf(AMIR, ROGA, SORK, BAND, KALP, MARA, CHOR, UTHI),
        Vara.MANGALAVARA to listOf(ROGA, SORK, BAND, KALP, MARA, CHOR, UTHI, AMIR),
        Vara.BUDHAVARA to listOf(SORK, BAND, KALP, MARA, CHOR, UTHI, AMIR, ROGA),
        Vara.GURUVARA to listOf(BAND, KALP, MARA, CHOR, UTHI, AMIR, ROGA, SORK),
        Vara.SHUKRAVARA to listOf(KALP, MARA, CHOR, UTHI, AMIR, ROGA, SORK, BAND),
        Vara.SHANIVARA to listOf(MARA, CHOR, UTHI, AMIR, ROGA, SORK, BAND, KALP)
    )

    private val NIGHT_GOWRI = mapOf(
        Vara.RAVIVARA to listOf(MARA, CHOR, UTHI, AMIR, ROGA, SORK, BAND, KALP),
        Vara.SOMAVARA to listOf(KALP, MARA, CHOR, UTHI, AMIR, ROGA, SORK, BAND),
        Vara.MANGALAVARA to listOf(BAND, KALP, MARA, CHOR, UTHI, AMIR, ROGA, SORK),
        Vara.BUDHAVARA to listOf(SORK, BAND, KALP, MARA, CHOR, UTHI, AMIR, ROGA),
        Vara.GURUVARA to listOf(ROGA, SORK, BAND, KALP, MARA, CHOR, UTHI, AMIR),
        Vara.SHUKRAVARA to listOf(AMIR, ROGA, SORK, BAND, KALP, MARA, CHOR, UTHI),
        Vara.SHANIVARA to listOf(UTHI, AMIR, ROGA, SORK, BAND, KALP, MARA, CHOR)
    )

    fun calculateGowri(
        sunrise: ZonedDateTime,
        sunset: ZonedDateTime,
        nextSunrise: ZonedDateTime,
        vara: Vara
    ): Pair<List<GowriSlot>, List<GowriSlot>> {
        val dayDuration = Duration.between(sunrise, sunset).toMillis() / 8.0
        val nightDuration = Duration.between(sunset, nextSunrise).toMillis() / 8.0
        val fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        val daySeq = DAY_GOWRI[vara] ?: DAY_GOWRI.values.first()
        val nightSeq = NIGHT_GOWRI[vara] ?: NIGHT_GOWRI.values.first()

        val daySlots = daySeq.mapIndexed { idx, def ->
            val start = sunrise.plusSeconds(((idx * dayDuration) / 1000.0).toLong())
            val end = if (idx == 7) sunset else sunrise.plusSeconds((((idx + 1) * dayDuration) / 1000.0).toLong())
            GowriSlot(
                index = idx + 1,
                isDay = true,
                nameEn = def.en,
                nameHi = def.hi,
                nameGu = def.gu,
                isAuspicious = def.isGood,
                startIso = start.format(fmt),
                endIso = end.format(fmt)
            )
        }

        val nightSlots = nightSeq.mapIndexed { idx, def ->
            val start = sunset.plusSeconds(((idx * nightDuration) / 1000.0).toLong())
            val end = if (idx == 7) nextSunrise else sunset.plusSeconds((((idx + 1) * nightDuration) / 1000.0).toLong())
            GowriSlot(
                index = idx + 1,
                isDay = false,
                nameEn = def.en,
                nameHi = def.hi,
                nameGu = def.gu,
                isAuspicious = def.isGood,
                startIso = start.format(fmt),
                endIso = end.format(fmt)
            )
        }

        return Pair(daySlots, nightSlots)
    }
}
