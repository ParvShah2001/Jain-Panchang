package com.jainpanchang.engine.astro

import com.jainpanchang.engine.model.Ayanamsha

object AyanamshaCalculator {

    /**
     * Compute Ayanamsha value in degrees for a given Julian Day.
     * Standard Lahiri Ayanamsha:
     * Base at epoch J2000.0 (JD 2451545.0) = 23.856994 degrees (23° 51' 25.18")
     * Precession rate = 50.290966 arcseconds per year.
     */
    fun calculateAyanamsha(jd: Double, type: Ayanamsha): Double {
        val tCenturies = (jd - 2451545.0) / 36525.0
        val tYears = (jd - 2451545.0) / 365.25

        return when (type) {
            Ayanamsha.LAHIRI -> {
                23.856994 + (50.290966 * tYears) / 3600.0 + (1.11 * tCenturies * tCenturies) / 3600.0
            }
            Ayanamsha.RAMAN -> {
                22.460278 + (50.290966 * tYears) / 3600.0
            }
            Ayanamsha.KRISHNAMURTI -> {
                23.824722 + (50.290966 * tYears) / 3600.0
            }
            Ayanamsha.SAYANA -> {
                0.0
            }
        }
    }

    /**
     * Convert Tropical (Sayana) ecliptic longitude to Sidereal (Nirayana) longitude.
     */
    fun toSidereal(tropicalLongitudeDeg: Double, ayanamshaDeg: Double): Double {
        var sidereal = (tropicalLongitudeDeg - ayanamshaDeg) % 360.0
        if (sidereal < 0.0) sidereal += 360.0
        return sidereal
    }
}
