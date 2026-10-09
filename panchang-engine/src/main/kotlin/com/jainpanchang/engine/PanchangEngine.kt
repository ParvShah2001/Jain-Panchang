package com.jainpanchang.engine

import com.jainpanchang.engine.astro.*
import com.jainpanchang.engine.model.*
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PanchangEngine {

    /**
     * Compute full DailyPanchang for given date, location, and ayanamsha.
     */
    fun calculateDailyPanchang(
        date: LocalDate,
        location: GeoLocation,
        ayanamsha: Ayanamsha = Ayanamsha.LAHIRI,
        kalyanaksData: List<Kalyanak> = emptyList(),
        festivalsData: List<Festival> = emptyList()
    ): DailyPanchang {
        val zoneId = location.zoneId

        // 1. Solar calculations
        val (sunriseToday, sunsetToday) = MeeusAstronomy.calculateSunriseSunset(
            date = date,
            latitude = location.latitude,
            longitude = location.longitude,
            zoneId = zoneId
        )

        val tomorrow = date.plusDays(1)
        val (sunriseTomorrow, _) = MeeusAstronomy.calculateSunriseSunset(
            date = tomorrow,
            latitude = location.latitude,
            longitude = location.longitude,
            zoneId = zoneId
        )

        val (moonrise, moonset) = MeeusAstronomy.calculateMoonriseMoonset(
            date = date,
            latitude = location.latitude,
            longitude = location.longitude,
            zoneId = zoneId
        )

        val dayDurationMinutes = Duration.between(sunriseToday, sunsetToday).toMinutes()
        val nightDurationMinutes = Duration.between(sunsetToday, sunriseTomorrow).toMinutes()
        val solarNoon = sunriseToday.plusMinutes(dayDurationMinutes / 2)

        val fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val solarTimes = SolarTimes(
            sunriseIso = sunriseToday.format(fmt),
            sunsetIso = sunsetToday.format(fmt),
            solarNoonIso = solarNoon.format(fmt),
            dayLengthMinutes = dayDurationMinutes,
            nightLengthMinutes = nightDurationMinutes
        )

        val lunarTimes = LunarTimes(
            moonriseIso = moonrise?.format(fmt),
            moonsetIso = moonset?.format(fmt)
        )

        // 2. Tithi at sunrise (Udaya Tithi)
        val sunriseInstant = sunriseToday.toInstant()
        val tithi = PanchangCalculator.computeUdayaTithi(sunriseInstant, zoneId)

        // 3. Nakshatra, Yoga, Karana, Vara
        val nakshatra = PanchangCalculator.computeNakshatra(sunriseInstant, ayanamsha, zoneId)
        val yoga = PanchangCalculator.computeYoga(sunriseInstant, ayanamsha, zoneId)
        val karana = PanchangCalculator.computeKarana(sunriseInstant)
        val vara = PanchangCalculator.computeVara(sunriseToday)

        // 4. Jain Month & Samvat Eras
        val (jainMonth, isAdhika) = PanchangCalculator.computeJainMonth(sunriseInstant, ayanamsha)
        val (vikramSamvat, veerNirvanSamvat) = PanchangCalculator.computeSamvat(date, jainMonth)

        // 5. Choghadiyas
        val dayChoghadiyas = ChoghadiyaCalculator.calculateDayChoghadiya(sunriseToday, sunsetToday, vara)
        val nightChoghadiyas = ChoghadiyaCalculator.calculateNightChoghadiya(sunsetToday, sunriseTomorrow, vara)

        // 6. Horas
        val horas = HoraCalculator.calculateHoras(sunriseToday, sunsetToday, sunriseTomorrow, vara)

        // 7. Gowri Panchangam
        val (gowriDay, gowriNight) = GowriCalculator.calculateGowri(sunriseToday, sunsetToday, sunriseTomorrow, vara)

        // 8. Muhurats
        val muhurats = MuhuratCalculator.calculateMuhurats(sunriseToday, sunsetToday, vara)

        // 9. Pachkhans
        val pachkhans = PachkhanCalculator.calculatePachkhans(sunriseToday, sunsetToday)

        // 10. Filter Kalyanaks and Festivals for today's Jain month, paksha, tithi
        val kalyanaksToday = kalyanaksData.filter {
            it.jainMonth == jainMonth && it.paksha == tithi.paksha && it.tithi == tithi.number
        }
        val festivalsToday = festivalsData.filter {
            it.jainMonth == jainMonth && it.paksha == tithi.paksha && it.tithi == tithi.number
        }

        return DailyPanchang(
            dateIso = date.toString(),
            location = location,
            solarTimes = solarTimes,
            lunarTimes = lunarTimes,
            tithi = tithi,
            nakshatra = nakshatra,
            yoga = yoga,
            karana = karana,
            vara = vara,
            jainMonth = jainMonth,
            isAdhikaMonth = isAdhika,
            vikramSamvat = vikramSamvat,
            veerNirvanSamvat = veerNirvanSamvat,
            dayChoghadiya = dayChoghadiyas,
            nightChoghadiya = nightChoghadiyas,
            horas = horas,
            gowriDay = gowriDay,
            gowriNight = gowriNight,
            muhurats = muhurats,
            pachkhans = pachkhans,
            kalyanaksToday = kalyanaksToday,
            festivalsToday = festivalsToday
        )
    }
}
