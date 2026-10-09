package com.jainpanchang.engine.astro

import com.jainpanchang.engine.model.MuhuratPeriod
import com.jainpanchang.engine.model.Vara
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object MuhuratCalculator {

    // 1-based daytime 1/8th segment indices for Rahu Kalam, Yamaganda, Gulika
    private val RAHU_SEGMENTS = mapOf(
        Vara.RAVIVARA to 8,
        Vara.SOMAVARA to 2,
        Vara.MANGALAVARA to 7,
        Vara.BUDHAVARA to 5,
        Vara.GURUVARA to 6,
        Vara.SHUKRAVARA to 4,
        Vara.SHANIVARA to 3
    )

    private val YAMAGANDA_SEGMENTS = mapOf(
        Vara.RAVIVARA to 5,
        Vara.SOMAVARA to 4,
        Vara.MANGALAVARA to 3,
        Vara.BUDHAVARA to 2,
        Vara.GURUVARA to 1,
        Vara.SHUKRAVARA to 7,
        Vara.SHANIVARA to 6
    )

    private val GULIKA_SEGMENTS = mapOf(
        Vara.RAVIVARA to 7,
        Vara.SOMAVARA to 6,
        Vara.MANGALAVARA to 5,
        Vara.BUDHAVARA to 4,
        Vara.GURUVARA to 3,
        Vara.SHUKRAVARA to 2,
        Vara.SHANIVARA to 1
    )

    fun calculateMuhurats(
        sunrise: ZonedDateTime,
        sunset: ZonedDateTime,
        vara: Vara
    ): List<MuhuratPeriod> {
        val dayDuration = Duration.between(sunrise, sunset).toMillis()
        val partDuration = dayDuration / 8.0
        val muhuratDuration = dayDuration / 15.0 // 15 Muhurats in daytime
        val fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val list = mutableListOf<MuhuratPeriod>()

        fun getSegment(segmentNum: Int): Pair<ZonedDateTime, ZonedDateTime> {
            val sIdx = segmentNum - 1
            val start = sunrise.plusSeconds(((sIdx * partDuration) / 1000.0).toLong())
            val end = if (segmentNum == 8) sunset else sunrise.plusSeconds((((sIdx + 1) * partDuration) / 1000.0).toLong())
            return Pair(start, end)
        }

        // 1. Abhijit Muhurat (8th Muhurat of day: from 7/15 to 8/15 of daytime)
        val abhijitStart = sunrise.plusSeconds(((7 * muhuratDuration) / 1000.0).toLong())
        val abhijitEnd = sunrise.plusSeconds(((8 * muhuratDuration) / 1000.0).toLong())
        list.add(
            MuhuratPeriod(
                id = "abhijit",
                nameEn = "Abhijit Muhurat",
                nameHi = "अभिजित मुहूर्त",
                nameGu = "અભિજિત મુહૂર્ત",
                isAuspicious = true,
                startIso = abhijitStart.format(fmt),
                endIso = abhijitEnd.format(fmt),
                description = "Highly auspicious midday interval for commencing benevolent endeavors."
            )
        )

        // 2. Rahu Kalam
        val rahuSeg = RAHU_SEGMENTS[vara] ?: 8
        val (rahuStart, rahuEnd) = getSegment(rahuSeg)
        list.add(
            MuhuratPeriod(
                id = "rahu_kalam",
                nameEn = "Rahu Kalam",
                nameHi = "राहु काल",
                nameGu = "રાહુ કાળ",
                isAuspicious = false,
                startIso = rahuStart.format(fmt),
                endIso = rahuEnd.format(fmt),
                description = "Inauspicious planetary window under Rahu."
            )
        )

        // 3. Yamaganda Kalam
        val yamaSeg = YAMAGANDA_SEGMENTS[vara] ?: 5
        val (yamaStart, yamaEnd) = getSegment(yamaSeg)
        list.add(
            MuhuratPeriod(
                id = "yamaganda",
                nameEn = "Yamaganda Kalam",
                nameHi = "यमगण्ड काल",
                nameGu = "યમગંડ કાળ",
                isAuspicious = false,
                startIso = yamaStart.format(fmt),
                endIso = yamaEnd.format(fmt),
                description = "Inauspicious window ruled by Yama."
            )
        )

        // 4. Gulika Kalam
        val guliSeg = GULIKA_SEGMENTS[vara] ?: 7
        val (guliStart, guliEnd) = getSegment(guliSeg)
        list.add(
            MuhuratPeriod(
                id = "gulika",
                nameEn = "Gulika Kalam",
                nameHi = "गुलिक काल",
                nameGu = "ગુલિક કાળ",
                isAuspicious = true, // Often considered favorable for beginning long-term projects
                startIso = guliStart.format(fmt),
                endIso = guliEnd.format(fmt),
                description = "Auspicious window governed by Saturn's benevolent son Gulika."
            )
        )

        // 5. Dur Muhurat (e.g. 1st or 2nd muhurat based on day)
        val durStart = sunrise.plusSeconds(((3 * muhuratDuration) / 1000.0).toLong())
        val durEnd = sunrise.plusSeconds(((4 * muhuratDuration) / 1000.0).toLong())
        list.add(
            MuhuratPeriod(
                id = "dur_muhurat",
                nameEn = "Dur Muhurat",
                nameHi = "दुर्मुहूर्त",
                nameGu = "દુર્મુહૂર્ત",
                isAuspicious = false,
                startIso = durStart.format(fmt),
                endIso = durEnd.format(fmt),
                description = "Inauspicious timing window to be avoided for solemn undertakings."
            )
        )

        return list
    }
}
