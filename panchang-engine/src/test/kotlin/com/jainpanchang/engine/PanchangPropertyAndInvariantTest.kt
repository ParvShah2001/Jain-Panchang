package com.jainpanchang.engine

import com.jainpanchang.engine.astro.*
import com.jainpanchang.engine.model.*
import org.junit.Assert.*
import org.junit.Test
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime

class PanchangPropertyAndInvariantTest {

    private val engine = PanchangEngine()

    private val testLocations = listOf(
        GeoLocation("Mumbai", 18.9220, 72.8347, 10.0, "Asia/Kolkata"),
        GeoLocation("Ahmedabad", 23.0225, 72.5714, 53.0, "Asia/Kolkata"),
        GeoLocation("Palitana", 21.5222, 71.8291, 66.0, "Asia/Kolkata"),
        GeoLocation("Shikharji", 23.9634, 86.1557, 1350.0, "Asia/Kolkata"),
        GeoLocation("London", 51.5074, -0.1278, 15.0, "Europe/London"),
        GeoLocation("New York", 40.7128, -74.0060, 10.0, "America/New_York")
    )

    private val sampleDates = listOf(
        LocalDate.of(2024, 1, 1),
        LocalDate.of(2024, 3, 21), // Spring equinox
        LocalDate.of(2024, 6, 21), // Summer solstice
        LocalDate.of(2024, 9, 23), // Autumn equinox
        LocalDate.of(2024, 12, 21), // Winter solstice
        LocalDate.of(2026, 10, 9), // Current year
        LocalDate.of(2050, 5, 15)  // Future date (spanning decades)
    )

    @Test
    fun testSolarTimesInvariants() {
        for (loc in testLocations) {
            for (date in sampleDates) {
                val panchang = engine.calculateDailyPanchang(date, loc)
                val sunrise = ZonedDateTime.parse(panchang.solarTimes.sunriseIso)
                val sunset = ZonedDateTime.parse(panchang.solarTimes.sunsetIso)

                // Invariant: sunrise < sunset
                assertTrue("Sunrise must be before sunset on $date at ${loc.name}", sunrise.isBefore(sunset))

                // Invariant: day length between 6 and 18 hours for non-polar locations
                val dayHours = panchang.solarTimes.dayLengthMinutes / 60.0
                assertTrue("Day length reasonable ($dayHours hrs) on $date at ${loc.name}", dayHours in 6.0..18.0)
            }
        }
    }

    @Test
    fun testChoghadiyaSumAndPartInvariants() {
        for (loc in testLocations) {
            for (date in sampleDates) {
                val panchang = engine.calculateDailyPanchang(date, loc)
                val sunrise = ZonedDateTime.parse(panchang.solarTimes.sunriseIso)
                val sunset = ZonedDateTime.parse(panchang.solarTimes.sunsetIso)

                assertEquals("Must have 8 day Choghadiyas", 8, panchang.dayChoghadiya.size)
                assertEquals("Must have 8 night Choghadiyas", 8, panchang.nightChoghadiya.size)

                // First day choghadiya starts at sunrise
                assertEquals(
                    sunrise.toInstant().epochSecond,
                    ZonedDateTime.parse(panchang.dayChoghadiya.first().startIso).toInstant().epochSecond
                )

                // Last day choghadiya ends at sunset
                assertEquals(
                    sunset.toInstant().epochSecond,
                    ZonedDateTime.parse(panchang.dayChoghadiya.last().endIso).toInstant().epochSecond
                )

                // Sum of durations of all 8 parts equals daytime length
                var totalDayPartSeconds = 0L
                for (ch in panchang.dayChoghadiya) {
                    val s = ZonedDateTime.parse(ch.startIso)
                    val e = ZonedDateTime.parse(ch.endIso)
                    assertTrue("Choghadiya slot start < end", s.isBefore(e))
                    totalDayPartSeconds += Duration.between(s, e).seconds
                }
                val actualDaySeconds = Duration.between(sunrise, sunset).seconds
                assertEquals(
                    "Sum of 8 day choghadiyas must equal day length",
                    actualDaySeconds,
                    totalDayPartSeconds
                )
            }
        }
    }

    @Test
    fun testPachkhanOrderInvariants() {
        val loc = testLocations.first() // Mumbai
        for (date in sampleDates) {
            val panchang = engine.calculateDailyPanchang(date, loc)
            val sunrise = ZonedDateTime.parse(panchang.solarTimes.sunriseIso)
            val sunset = ZonedDateTime.parse(panchang.solarTimes.sunsetIso)

            val pMap = panchang.pachkhans.associateBy { it.id }
            val navkarshi = ZonedDateTime.parse(pMap["navkarshi"]!!.timeIso)
            val porsi = ZonedDateTime.parse(pMap["porsi"]!!.timeIso)
            val sadhPorsi = ZonedDateTime.parse(pMap["sadh_porsi"]!!.timeIso)
            val purimuddh = ZonedDateTime.parse(pMap["purimuddh"]!!.timeIso)
            val avaddh = ZonedDateTime.parse(pMap["avaddh"]!!.timeIso)
            val chauvihar = ZonedDateTime.parse(pMap["chauvihar"]!!.timeIso)

            // Invariant: Navkarshi is strictly 48 minutes after sunrise
            assertEquals(
                "Navkarshi must be sunrise + 48 min",
                sunrise.plusMinutes(48).toInstant().epochSecond,
                navkarshi.toInstant().epochSecond
            )

            // Invariant: Strict sequential progression of daytime austerities
            assertTrue("Sunrise < Navkarshi", sunrise.isBefore(navkarshi))
            assertTrue("Navkarshi <= Porsi", navkarshi.isBefore(porsi) || navkarshi.isEqual(porsi))
            assertTrue("Porsi < Sadh Porsi", porsi.isBefore(sadhPorsi))
            assertTrue("Sadh Porsi < Purimuddh", sadhPorsi.isBefore(purimuddh))
            assertTrue("Purimuddh < Avaddh", purimuddh.isBefore(avaddh))
            assertTrue("Avaddh < Chauvihar", avaddh.isBefore(chauvihar))

            // Invariant: Chauvihar is exactly at sunset
            assertEquals(
                "Chauvihar must match sunset",
                sunset.toInstant().epochSecond,
                chauvihar.toInstant().epochSecond
            )
        }
    }

    @Test
    fun testPanchangCoreElementsRanges() {
        for (loc in testLocations) {
            for (date in sampleDates) {
                val panchang = engine.calculateDailyPanchang(date, loc)

                // Tithi in 1..15
                assertTrue("Tithi number in 1..15: ${panchang.tithi.number}", panchang.tithi.number in 1..15)

                // Nakshatra in 1..27
                assertTrue("Nakshatra in 1..27: ${panchang.nakshatra.index}", panchang.nakshatra.index in 1..27)

                // Yoga in 1..27
                assertTrue("Yoga in 1..27: ${panchang.yoga.index}", panchang.yoga.index in 1..27)

                // Karana in 1..60
                assertTrue("Karana in 1..60: ${panchang.karana.index}", panchang.karana.index in 1..60)

                // Samvat eras positive and coherent
                assertTrue("Vikram Samvat valid", panchang.vikramSamvat > 2000)
                assertEquals(
                    "Veer Nirvan Samvat offset must be +470 from VS",
                    panchang.vikramSamvat + 470,
                    panchang.veerNirvanSamvat
                )
            }
        }
    }

    @Test
    fun testHorasAndGowriInvariants() {
        val loc = testLocations.first()
        val panchang = engine.calculateDailyPanchang(LocalDate.of(2026, 10, 9), loc)

        // Exactly 24 Horas
        assertEquals("Must have 24 Horas", 24, panchang.horas.size)

        // Exactly 8 day Gowri and 8 night Gowri slots
        assertEquals("8 day Gowri", 8, panchang.gowriDay.size)
        assertEquals("8 night Gowri", 8, panchang.gowriNight.size)

        // Muhurats must include Abhijit and Rahu Kalam
        val mIds = panchang.muhurats.map { it.id }.toSet()
        assertTrue("Must have Abhijit", mIds.contains("abhijit"))
        assertTrue("Must have Rahu Kalam", mIds.contains("rahu_kalam"))
        assertTrue("Must have Yamaganda", mIds.contains("yamaganda"))
        assertTrue("Must have Gulika", mIds.contains("gulika"))
    }
}
