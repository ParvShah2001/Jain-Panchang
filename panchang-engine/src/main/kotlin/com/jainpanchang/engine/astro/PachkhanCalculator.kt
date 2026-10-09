package com.jainpanchang.engine.astro

import com.jainpanchang.engine.model.PachkhanTiming
import java.time.Duration
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object PachkhanCalculator {

    fun calculatePachkhans(
        sunrise: ZonedDateTime,
        sunset: ZonedDateTime
    ): List<PachkhanTiming> {
        val dinmanMillis = Duration.between(sunrise, sunset).toMillis()
        val fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        fun offsetSunriseMin(min: Long): ZonedDateTime = sunrise.plusMinutes(min)
        fun dinmanFraction(numerator: Long, denominator: Long): ZonedDateTime {
            val millis = (dinmanMillis * numerator) / denominator
            return sunrise.plusSeconds(millis / 1000)
        }

        val navkarshi = offsetSunriseMin(48)
        val porsi = dinmanFraction(1, 4)
        val sadhPorsi = dinmanFraction(3, 8)
        val purimuddh = dinmanFraction(1, 2)
        val avaddh = dinmanFraction(3, 4)
        val chauvihar = sunset

        return listOf(
            PachkhanTiming(
                id = "navkarshi",
                nameEn = "Navkarshi",
                nameHi = "नवकारशी",
                nameGu = "નવકારશી",
                timeIso = navkarshi.format(fmt),
                formulaType = "SUNRISE_OFFSET_MINUTES",
                description = "48 minutes after local astronomical sunrise",
                needsReview = false
            ),
            PachkhanTiming(
                id = "porsi",
                nameEn = "Porsi",
                nameHi = "पोरसी",
                nameGu = "પોરસી",
                timeIso = porsi.format(fmt),
                formulaType = "DINMAN_FRACTION",
                description = "One Prahar (1/4th of day length) after sunrise",
                needsReview = false
            ),
            PachkhanTiming(
                id = "sadh_porsi",
                nameEn = "Sadh Porsi",
                nameHi = "साढ़ पोरસી",
                nameGu = "સાઢ પોરસી",
                timeIso = sadhPorsi.format(fmt),
                formulaType = "DINMAN_FRACTION",
                description = "One and a half Prahars (3/8th of day length) after sunrise",
                needsReview = false
            ),
            PachkhanTiming(
                id = "purimuddh",
                nameEn = "Purimuddh",
                nameHi = "पुरिमड्ढ",
                nameGu = "પુરીમડ્ઢ",
                timeIso = purimuddh.format(fmt),
                formulaType = "DINMAN_FRACTION",
                description = "Two Prahars (Midday / half of daytime) after sunrise",
                needsReview = false
            ),
            PachkhanTiming(
                id = "avaddh",
                nameEn = "Avaddh",
                nameHi = "अवड्ढ",
                nameGu = "અવડ્ઢ",
                timeIso = avaddh.format(fmt),
                formulaType = "DINMAN_FRACTION",
                description = "Three Prahars (3/4th of daytime) after sunrise",
                needsReview = false
            ),
            PachkhanTiming(
                id = "chauvihar",
                nameEn = "Chauvihar",
                nameHi = "चौविहार",
                nameGu = "ચૌવિહાર",
                timeIso = chauvihar.format(fmt),
                formulaType = "EXACT_SUNSET",
                description = "Exact local sunset (all food and liquids renounced until tomorrow)",
                needsReview = false
            ),
            PachkhanTiming(
                id = "tivihar",
                nameEn = "Tivihar",
                nameHi = "तिविहार",
                nameGu = "તિવિહાર",
                timeIso = chauvihar.format(fmt),
                formulaType = "EXACT_SUNSET",
                description = "Fast with boiled water permitted during daylight hours until sunset",
                needsReview = false
            )
        )
    }
}
