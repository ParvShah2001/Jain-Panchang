package com.jainpanchang.engine.astro

import java.time.*
import kotlin.math.*

/**
 * Astronomical computations based on Jean Meeus, "Astronomical Algorithms" (2nd Edition).
 * Pure Kotlin/JVM with zero external dependencies.
 */
object MeeusAstronomy {

    private const val PI2 = 2.0 * Math.PI
    private const val DEG_TO_RAD = Math.PI / 180.0
    private const val RAD_TO_DEG = 180.0 / Math.PI

    private fun normalizeDegrees(deg: Double): Double {
        var d = deg % 360.0
        if (d < 0.0) d += 360.0
        return d
    }

    private fun normalizeRadians(rad: Double): Double {
        var r = rad % PI2
        if (r < 0.0) r += PI2
        return r
    }

    /**
     * Julian Day Number from UTC Instant.
     */
    fun toJulianDay(instant: Instant): Double {
        val epochMilli = instant.toEpochMilli()
        return 2440587.5 + (epochMilli / 86400000.0)
    }

    /**
     * Centuries since J2000.0 (JD 2451545.0).
     */
    fun julianCenturies(jd: Double): Double = (jd - 2451545.0) / 36525.0

    /**
     * Apparent Solar Coordinates (Geocentric).
     * Returns Triple(eclipticLongitudeDeg, rightAscensionHours, declinationDeg).
     */
    fun getSunCoordinates(jd: Double): SunCoords {
        val T = julianCenturies(jd)

        // Geometric mean longitude of the Sun (Meeus 25.2)
        val L0 = normalizeDegrees(280.46646 + 36000.76983 * T + 0.0003032 * T * T)

        // Mean anomaly of the Sun (Meeus 25.3)
        val M = normalizeDegrees(357.52911 + 35999.05029 * T - 0.0001537 * T * T)
        val Mrad = M * DEG_TO_RAD

        // Sun equation of center (Meeus 25.4)
        val C = (1.914602 - 0.004817 * T - 0.000014 * T * T) * sin(Mrad) +
                (0.019993 - 0.000101 * T) * sin(2.0 * Mrad) +
                0.000289 * sin(3.0 * Mrad)

        // True longitude
        val sunTrueLon = L0 + C

        // Apparent longitude (corrected for nutation and aberration)
        val omega = 125.04 - 1934.136 * T
        val lambda = normalizeDegrees(sunTrueLon - 0.00569 - 0.00478 * sin(omega * DEG_TO_RAD))

        // Mean obliquity of ecliptic (Meeus 22.2)
        val eps0 = 23.4392911 - (46.8150 * T + 0.00059 * T * T - 0.001813 * T * T * T) / 3600.0
        val eps = (eps0 + 0.00256 * cos(omega * DEG_TO_RAD)) * DEG_TO_RAD

        // Equatorial coordinates
        val lambdaRad = lambda * DEG_TO_RAD
        val sinDelta = sin(eps) * sin(lambdaRad)
        val delta = asin(sinDelta) * RAD_TO_DEG
        val alpha = atan2(cos(eps) * sin(lambdaRad), cos(lambdaRad)) * RAD_TO_DEG
        val raHours = normalizeDegrees(alpha) / 15.0

        return SunCoords(
            eclipticLongitude = lambda,
            rightAscensionHours = raHours,
            declinationDegrees = delta
        )
    }

    /**
     * Apparent Lunar Coordinates (Meeus Chapter 47).
     * Returns MoonCoords(eclipticLongitude, eclipticLatitude, distanceKm).
     */
    fun getMoonCoordinates(jd: Double): MoonCoords {
        val T = julianCenturies(jd)

        // Moon's mean longitude (Meeus 47.1)
        val Lprime = normalizeDegrees(218.3164477 + 481267.88123421 * T - 0.0015786 * T * T + T * T * T / 538841.0)

        // Moon's mean elongation
        val D = normalizeDegrees(297.8501921 + 445267.1114034 * T - 0.0018819 * T * T + T * T * T / 545868.0)

        // Sun's mean anomaly
        val M = normalizeDegrees(357.5291092 + 35999.0502909 * T - 0.0001536 * T * T + T * T * T / 24490000.0)

        // Moon's mean anomaly
        val Mprime = normalizeDegrees(134.9633964 + 477198.8675055 * T + 0.0087414 * T * T + T * T * T / 69699.0)

        // Moon's argument of latitude
        val F = normalizeDegrees(93.2720950 + 483202.0175233 * T - 0.0036539 * T * T - T * T * T / 3526000.0)

        val Drad = D * DEG_TO_RAD
        val Mrad = M * DEG_TO_RAD
        val Mprad = Mprime * DEG_TO_RAD
        val Frad = F * DEG_TO_RAD

        // Periodic terms for longitude (Meeus Table 47.A - prominent terms)
        var sigmaL = 6.288774 * sin(Mprad)
        sigmaL += 1.274027 * sin(2.0 * Drad - Mprad)
        sigmaL += 0.658314 * sin(2.0 * Drad)
        sigmaL += 0.213618 * sin(2.0 * Mprad)
        sigmaL -= 0.185116 * sin(Mrad)
        sigmaL -= 0.114332 * sin(2.0 * Frad)
        sigmaL += 0.058793 * sin(2.0 * Drad - 2.0 * Mprad)
        sigmaL += 0.057066 * sin(2.0 * Drad - Mrad - Mprad)
        sigmaL += 0.053322 * sin(2.0 * Drad + Mprad)
        sigmaL += 0.045758 * sin(2.0 * Drad - Mrad)
        sigmaL -= 0.040923 * sin(Mrad - Mprad)
        sigmaL -= 0.034720 * sin(Drad)
        sigmaL -= 0.030383 * sin(Mrad + Mprad)
        sigmaL += 0.015327 * sin(2.0 * Drad - 2.0 * Frad)
        sigmaL -= 0.012528 * sin(Mprad + 2.0 * Frad)
        sigmaL += 0.010980 * sin(Mprad - 2.0 * Frad)

        // Periodic terms for latitude (Meeus Table 47.B - prominent terms)
        var sigmaB = 5.128122 * sin(Frad)
        sigmaB += 0.280602 * sin(Mprad + Frad)
        sigmaB += 0.277693 * sin(Mprad - Frad)
        sigmaB += 0.173237 * sin(2.0 * Drad - Frad)
        sigmaB += 0.055413 * sin(2.0 * Drad - Mprad + Frad)
        sigmaB += 0.046271 * sin(2.0 * Drad - Mprad - Frad)
        sigmaB += 0.032573 * sin(2.0 * Drad + Frad)
        sigmaB += 0.017198 * sin(2.0 * Mprad + Frad)

        val moonLon = normalizeDegrees(Lprime + sigmaL)
        val moonLat = sigmaB

        // Equatorial coordinates
        val eps = (23.4392911 - 46.8150 * T / 3600.0) * DEG_TO_RAD
        val lambdaRad = moonLon * DEG_TO_RAD
        val betaRad = moonLat * DEG_TO_RAD

        val sinDelta = sin(betaRad) * cos(eps) + cos(betaRad) * sin(eps) * sin(lambdaRad)
        val delta = asin(sinDelta) * RAD_TO_DEG
        val y = sin(lambdaRad) * cos(eps) - tan(betaRad) * sin(eps)
        val x = cos(lambdaRad)
        val alpha = normalizeDegrees(atan2(y, x) * RAD_TO_DEG)
        val raHours = alpha / 15.0

        return MoonCoords(
            eclipticLongitude = moonLon,
            eclipticLatitude = moonLat,
            rightAscensionHours = raHours,
            declinationDegrees = delta
        )
    }

    /**
     * Greenwich Mean Sidereal Time in hours.
     */
    fun gmstHours(jd: Double): Double {
        val T = julianCenturies(jd)
        val gmst = 280.46061837 + 360.98564736629 * (jd - 2451545.0) +
                0.000387933 * T * T - (T * T * T) / 38710000.0
        return normalizeDegrees(gmst) / 15.0
    }

    /**
     * Calculate Equation of Time in minutes using Meeus chapter 28.
     */
    fun calculateEquationOfTime(jd: Double): Double {
        val T = julianCenturies(jd)
        val L0 = normalizeDegrees(280.46646 + 36000.76983 * T + 0.0003032 * T * T)
        val M = normalizeDegrees(357.52911 + 35999.05029 * T - 0.0001537 * T * T) * DEG_TO_RAD
        val e = 0.016708634 - 0.000042037 * T - 0.0000001267 * T * T

        val eps0 = 23.4392911 - (46.8150 * T + 0.00059 * T * T - 0.001813 * T * T * T) / 3600.0
        val eps = eps0 * DEG_TO_RAD
        val y = tan(eps / 2.0).pow(2)

        val L0rad = L0 * DEG_TO_RAD
        val eTime = y * sin(2.0 * L0rad) - 2.0 * e * sin(M) +
                4.0 * e * y * sin(M) * cos(2.0 * L0rad) -
                0.5 * y * y * sin(4.0 * L0rad) -
                1.25 * e * e * sin(2.0 * M)

        return eTime * RAD_TO_DEG * 4.0 // in minutes
    }

    /**
     * Calculate Sunrise and Sunset for a given date, latitude, and longitude.
     * Standard atmospheric refraction + solar semi-diameter = -0.8333 degrees.
     */
    fun calculateSunriseSunset(
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        zoneId: ZoneId
    ): Pair<ZonedDateTime, ZonedDateTime> {
        val noonUtc = date.atTime(12, 0).atZone(ZoneOffset.UTC)
        val jdNoon = toJulianDay(noonUtc.toInstant())

        val sun = getSunCoordinates(jdNoon)
        val latRad = latitude * DEG_TO_RAD
        val decRad = sun.declinationDegrees * DEG_TO_RAD

        // Horizon angle: h0 = -0.8333 degrees (refraction + sun radius)
        val h0 = -0.8333 * DEG_TO_RAD
        val cosH0 = (sin(h0) - sin(latRad) * sin(decRad)) / (cos(latRad) * cos(decRad))

        val hourAngleDeg = when {
            cosH0 > 1.0 -> 0.0 // Polar night
            cosH0 < -1.0 -> 180.0 // Midnight sun
            else -> acos(cosH0) * RAD_TO_DEG
        }

        // Solar transit in UTC hours: 12 - EoT/60 - Lon/15
        val eotMinutes = calculateEquationOfTime(jdNoon)
        val transitHoursUtc = 12.0 - (eotMinutes / 60.0) - (longitude / 15.0)

        val haHours = hourAngleDeg / 15.0
        val riseHoursUtc = (transitHoursUtc - haHours)
        val setHoursUtc = (transitHoursUtc + haHours)

        fun hoursToZdt(hrs: Double): ZonedDateTime {
            val normalizedHours = (hrs % 24.0 + 24.0) % 24.0
            val totalSeconds = (normalizedHours * 3600.0).roundToLong()
            val sec = (totalSeconds % 60).toInt()
            val min = ((totalSeconds / 60) % 60).toInt()
            val hour = ((totalSeconds / 3600) % 24).toInt()

            // If rise/set crossed into previous or next day in UTC
            val dayOffset = floor(hrs / 24.0).toLong()
            val actualDate = date.plusDays(dayOffset)
            val utcDt = actualDate.atTime(hour, min, sec).atZone(ZoneOffset.UTC)
            return utcDt.withZoneSameInstant(zoneId)
        }

        return Pair(hoursToZdt(riseHoursUtc), hoursToZdt(setHoursUtc))
    }

    /**
     * Approximate Moonrise and Moonset for a given date and location.
     */
    fun calculateMoonriseMoonset(
        date: LocalDate,
        latitude: Double,
        longitude: Double,
        zoneId: ZoneId
    ): Pair<ZonedDateTime?, ZonedDateTime?> {
        val noonUtc = date.atTime(12, 0).atZone(ZoneOffset.UTC)
        val jdNoon = toJulianDay(noonUtc.toInstant())
        val moon = getMoonCoordinates(jdNoon)

        val latRad = latitude * DEG_TO_RAD
        val decRad = moon.declinationDegrees * DEG_TO_RAD
        // h0 = 0.125 degrees for Moon (parallax - refraction)
        val h0 = 0.125 * DEG_TO_RAD
        val cosH0 = (sin(h0) - sin(latRad) * sin(decRad)) / (cos(latRad) * cos(decRad))

        if (cosH0 > 1.0 || cosH0 < -1.0) {
            return Pair(null, null)
        }

        val hourAngleDeg = acos(cosH0) * RAD_TO_DEG
        val gmstNoon = gmstHours(jdNoon)
        var transitHours = moon.rightAscensionHours - gmstNoon - (longitude / 15.0)
        transitHours = (transitHours % 24.0 + 24.0) % 24.0

        val haHours = hourAngleDeg / 15.0
        val riseHoursUtc = (transitHours - haHours + 24.0) % 24.0
        val setHoursUtc = (transitHours + haHours) % 24.0

        fun hoursToZdt(hrs: Double): ZonedDateTime {
            val totalSeconds = (hrs * 3600.0).roundToLong()
            val sec = (totalSeconds % 60).toInt()
            val min = ((totalSeconds / 60) % 60).toInt()
            val hour = ((totalSeconds / 3600) % 24).toInt()
            val utcDt = date.atTime(hour, min, sec).atZone(ZoneOffset.UTC)
            return utcDt.withZoneSameInstant(zoneId)
        }

        return Pair(hoursToZdt(riseHoursUtc), hoursToZdt(setHoursUtc))
    }
}

data class SunCoords(
    val eclipticLongitude: Double,
    val rightAscensionHours: Double,
    val declinationDegrees: Double
)

data class MoonCoords(
    val eclipticLongitude: Double,
    val eclipticLatitude: Double,
    val rightAscensionHours: Double,
    val declinationDegrees: Double
)
