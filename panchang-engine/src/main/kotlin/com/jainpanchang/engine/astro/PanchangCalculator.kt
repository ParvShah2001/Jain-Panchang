package com.jainpanchang.engine.astro

import com.jainpanchang.engine.model.*
import java.time.*
import java.time.format.DateTimeFormatter
import kotlin.math.floor

object PanchangCalculator {

    private val TITHI_NAMES_EN = listOf(
        "Pratipada (Ekam)", "Dwitiya (Beej)", "Tritiya (Trij)", "Chaturthi (Chauth)", "Panchami (Pancham)",
        "Shashthi (Chhath)", "Saptami (Satam)", "Ashtami (Aatham)", "Navami (Nom)", "Dashami (Dasham)",
        "Ekadashi (Agiyaras)", "Dwadashi (Baras)", "Trayodashi (Teras)", "Chaturdashi (Chaudas)", "Purnima / Amavasya"
    )

    private val TITHI_NAMES_HI = listOf(
        "प्रतिपदा (एकम)", "द्वितीया (बीज)", "तृतीया (तीज)", "चतुर्थी (चौथ)", "पंचमी",
        "षष्ठी (छठ)", "सप्तमी (सातम)", "अष्टमी (आठम)", "नवमी (नौमी)", "दशमी",
        "एकादशी (ग्यारस)", "द्वादशी (बारस)", "त्रयोदशी (तेरस)", "चतुर्दशी (चौदस)", "पूर्णिमा / अमावस्या"
    )

    private val TITHI_NAMES_GU = listOf(
        "પ્રતિપદા (એકમ)", "દ્વિતીયા (બીજ)", "તૃતીયા (ત્રીજ)", "ચતુર્થી (ચોથ)", "પંચમી (પાંચમ)",
        "ષષ્ઠી (છઠ)", "સપ્તમી (સાતમ)", "અષ્ટમી (આઠમ)", "નવમી (નોમ)", "દશમી (દસમ)",
        "એકાદશી (અગિયારસ)", "દ્વાદશી (બારસ)", "ત્રયોદશી (તેરસ)", "ચતુર્દશી (ચૌદસ)", "પૂનમ / અમાસ"
    )

    private val NAKSHATRA_NAMES = listOf(
        Triple("Ashvini", "अश्विनी", "અશ્વિની"),
        Triple("Bharani", "भरणी", "ભરણી"),
        Triple("Krittika", "कृत्तिका", "કૃત્તિકા"),
        Triple("Rohini", "रोहिणी", "રોહિણી"),
        Triple("Mrigashira", "मृगशिरा", "મૃગશીર્ષ"),
        Triple("Ardra", "आर्द्रा", "આર્દ્રા"),
        Triple("Punarvasu", "पुनर्वसु", "પુનર્વસુ"),
        Triple("Pushya", "पुष्य", "પુષ્ય"),
        Triple("Ashlesha", "आश्लेषा", "આશ્લેષા"),
        Triple("Magha", "मघा", "મઘા"),
        Triple("Purva Phalguni", "पूर्वाफाल्गुनी", "પૂર્વા ફાલ્ગુની"),
        Triple("Uttara Phalguni", "उत्तराफाल्गुनी", "ઉત્તરા ફાલ્ગુની"),
        Triple("Hasta", "हस्त", "હસ્ત"),
        Triple("Chitra", "चित्रा", "ચિત્રા"),
        Triple("Swati", "स्वाति", "સ્વાતિ"),
        Triple("Vishakha", "विशाखा", "વિશાખા"),
        Triple("Anuradha", "अनुराधा", "અનુરાધા"),
        Triple("Jyeshtha", "ज्येष्ठा", "જ્યેષ્ઠા"),
        Triple("Mula", "मूल", "મૂળ"),
        Triple("Purva Ashadha", "पूर्वाषाढ़ा", "પૂર્વાષાઢા"),
        Triple("Uttara Ashadha", "उत्तराषाढ़ा", "ઉત્તરાષાઢા"),
        Triple("Shravana", "श्रवण", "શ્રવણ"),
        Triple("Dhanishta", "धनिष्ठा", "ધનિષ્ઠા"),
        Triple("Shatabhisha", "शतभिषा", "શતભિષા"),
        Triple("Purva Bhadrapada", "पूर्वाभाद्रपद", "પૂર્વાભાદ્રપદ"),
        Triple("Uttara Bhadrapada", "उत्तराभाद्रपद", "ઉત્તરાભાદ્રપદ"),
        Triple("Revati", "रेवती", "રેવતી")
    )

    private val YOGA_NAMES = listOf(
        Triple("Vishkambha", "विष्कम्भ", "વિષ્કંભ"),
        Triple("Priti", "प्रीति", "પ્રીતિ"),
        Triple("Ayushman", "आयुष्मान्", "આયુષ્માન"),
        Triple("Saubhagya", "सौभाग्य", "સૌભાગ્ય"),
        Triple("Shobhana", "शोभन", "શોભન"),
        Triple("Atiganda", "अतिगण्ड", "અતિગંડ"),
        Triple("Sukarma", "सुकर्मा", "સુકર્મા"),
        Triple("Dhriti", "धृति", "ધૃતિ"),
        Triple("Shula", "शूल", "શૂલ"),
        Triple("Ganda", "गण्ड", "ગંડ"),
        Triple("Vriddhi", "वृद्धि", "વૃદ્ધિ"),
        Triple("Dhruva", "ध्रुव", "ધ્રુવ"),
        Triple("Vyaghata", "व्याघात", "વ્યાઘાત"),
        Triple("Harshana", "हर्षण", "હર્ષણ"),
        Triple("Vajra", "वज्र", "વજ્ર"),
        Triple("Siddhi", "सिद्धि", "સિદ્ધિ"),
        Triple("Vyatipata", "व्यतीपात", "વ્યતીપાત"),
        Triple("Variyan", "वरीयान्", "વરીયાન"),
        Triple("Parigha", "परिघ", "પરિઘ"),
        Triple("Shiva", "शिव", "શિવ"),
        Triple("Siddha", "सिद्ध", "સિદ્ધ"),
        Triple("Sadhya", "साध्य", "સાધ્ય"),
        Triple("Shubha", "शुभ", "શુભ"),
        Triple("Shukla", "शुक्ल", "શુક્લ"),
        Triple("Brahma", "ब्रह्म", "બ્રહ્મ"),
        Triple("Indra", "इन्द्र", "ઇન્દ્ર"),
        Triple("Vaidhriti", "वैधृति", "વૈધૃતિ")
    )

    private val KARANA_NAMES = listOf(
        Triple("Bava", "बव", "બવ"),
        Triple("Balava", "बालव", "બાલવ"),
        Triple("Kaulava", "कौलव", "કૌલવ"),
        Triple("Taitila", "तैतिल", "તૈતિલ"),
        Triple("Garija", "गरिज", "ગર"),
        Triple("Vanija", "वणिज", "વણિજ"),
        Triple("Vishti (Bhadra)", "विष्टि (भद्रा)", "વિષ્ટિ (ભદ્રા)"),
        Triple("Shakuni", "शकुनि", "શકુનિ"),
        Triple("Chatushpada", "चतुष्पाद", "ચતુષ્પાદ"),
        Triple("Naga", "नाग", "નાગ"),
        Triple("Kintughna", "किंस्तुघ्न", "કિન્સ્તુઘ્ન")
    )

    /**
     * Compute separation angle between Moon and Sun in range [0, 360).
     */
    fun getMoonSunElongation(instant: Instant): Double {
        val jd = MeeusAstronomy.toJulianDay(instant)
        val sun = MeeusAstronomy.getSunCoordinates(jd)
        val moon = MeeusAstronomy.getMoonCoordinates(jd)
        var diff = (moon.eclipticLongitude - sun.eclipticLongitude) % 360.0
        if (diff < 0.0) diff += 360.0
        return diff
    }

    /**
     * Tithi number from 1 to 30.
     * 1..15 = Shukla Paksha (15 is Purnima)
     * 16..30 = Krishna Paksha (30 is Amavasya)
     */
    fun calculateTithiNumber(instant: Instant): Int {
        val elongation = getMoonSunElongation(instant)
        return (floor(elongation / 12.0).toInt() % 30) + 1
    }

    /**
     * Finds the instant when the current tithi ends.
     */
    fun findTithiEnd(currentInstant: Instant, currentTithiNum: Int): Instant {
        val targetDeg = (currentTithiNum * 12.0) % 360.0

        // Search window: up to 28 hours ahead
        var low = currentInstant
        var high = currentInstant.plusSeconds(28 * 3600)

        // Bisection search (15 steps gives sub-second precision)
        for (i in 0 until 18) {
            val midMillis = (low.toEpochMilli() + high.toEpochMilli()) / 2
            val midInstant = Instant.ofEpochMilli(midMillis)
            val elong = getMoonSunElongation(midInstant)

            var diff = elong - targetDeg
            if (diff < -180.0) diff += 360.0
            if (diff > 180.0) diff -= 360.0

            if (diff < 0.0) {
                low = midInstant
            } else {
                high = midInstant
            }
        }
        return low
    }

    /**
     * Finds the instant when the current tithi started.
     */
    fun findTithiStart(currentInstant: Instant, currentTithiNum: Int): Instant {
        val targetDeg = (((currentTithiNum - 1) * 12.0) % 360.0 + 360.0) % 360.0

        var low = currentInstant.minusSeconds(28 * 3600)
        var high = currentInstant

        for (i in 0 until 18) {
            val midMillis = (low.toEpochMilli() + high.toEpochMilli()) / 2
            val midInstant = Instant.ofEpochMilli(midMillis)
            val elong = getMoonSunElongation(midInstant)

            var diff = elong - targetDeg
            if (diff < -180.0) diff += 360.0
            if (diff > 180.0) diff -= 360.0

            if (diff < 0.0) {
                low = midInstant
            } else {
                high = midInstant
            }
        }
        return high
    }

    /**
     * Compute full Tithi element at astronomical sunrise.
     */
    fun computeUdayaTithi(sunriseInstant: Instant, zoneId: ZoneId): Tithi {
        val rawNum = calculateTithiNumber(sunriseInstant) // 1..30
        val paksha = if (rawNum <= 15) Paksha.SHUKLA else Paksha.KRISHNA
        val tithiNumInPaksha = if (rawNum <= 15) rawNum else rawNum - 15

        val nameIdx = (tithiNumInPaksha - 1).coerceIn(0, 14)
        val nameEn = if (tithiNumInPaksha == 15) {
            if (paksha == Paksha.SHUKLA) "Purnima (Poonam)" else "Amavasya (Amas)"
        } else TITHI_NAMES_EN[nameIdx]

        val nameHi = if (tithiNumInPaksha == 15) {
            if (paksha == Paksha.SHUKLA) "पूर्णिमा" else "अमावस्या"
        } else TITHI_NAMES_HI[nameIdx]

        val nameGu = if (tithiNumInPaksha == 15) {
            if (paksha == Paksha.SHUKLA) "પૂનમ" else "અમાસ"
        } else TITHI_NAMES_GU[nameIdx]

        val start = findTithiStart(sunriseInstant, rawNum)
        val end = findTithiEnd(sunriseInstant, rawNum)

        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        return Tithi(
            number = tithiNumInPaksha,
            paksha = paksha,
            nameEn = nameEn,
            nameHi = nameHi,
            nameGu = nameGu,
            startIso = start.atZone(zoneId).format(formatter),
            endIso = end.atZone(zoneId).format(formatter)
        )
    }

    /**
     * Compute Nakshatra for given instant and ayanamsha.
     */
    fun computeNakshatra(instant: Instant, ayanamsha: Ayanamsha, zoneId: ZoneId): Nakshatra {
        val jd = MeeusAstronomy.toJulianDay(instant)
        val moon = MeeusAstronomy.getMoonCoordinates(jd)
        val ayanDeg = AyanamshaCalculator.calculateAyanamsha(jd, ayanamsha)
        val siderealMoon = AyanamshaCalculator.toSidereal(moon.eclipticLongitude, ayanDeg)

        val arcDeg = 360.0 / 27.0 // 13° 20' = 13.333333°
        val index = (floor(siderealMoon / arcDeg).toInt() % 27) + 1
        val names = NAKSHATRA_NAMES[(index - 1).coerceIn(0, 26)]

        // Approximate boundaries (+- 24 hours search)
        val startInstant = instant.minusSeconds(12 * 3600)
        val endInstant = instant.plusSeconds(12 * 3600)

        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        return Nakshatra(
            index = index,
            nameEn = names.first,
            nameHi = names.second,
            nameGu = names.third,
            startIso = startInstant.atZone(zoneId).format(formatter),
            endIso = endInstant.atZone(zoneId).format(formatter)
        )
    }

    /**
     * Compute Yoga for given instant.
     */
    fun computeYoga(instant: Instant, ayanamsha: Ayanamsha, zoneId: ZoneId): Yoga {
        val jd = MeeusAstronomy.toJulianDay(instant)
        val sun = MeeusAstronomy.getSunCoordinates(jd)
        val moon = MeeusAstronomy.getMoonCoordinates(jd)
        val ayanDeg = AyanamshaCalculator.calculateAyanamsha(jd, ayanamsha)

        val siderealSun = AyanamshaCalculator.toSidereal(sun.eclipticLongitude, ayanDeg)
        val siderealMoon = AyanamshaCalculator.toSidereal(moon.eclipticLongitude, ayanDeg)

        val sum = (siderealSun + siderealMoon) % 360.0
        val arcDeg = 360.0 / 27.0
        val index = (floor(sum / arcDeg).toInt() % 27) + 1
        val names = YOGA_NAMES[(index - 1).coerceIn(0, 26)]

        val startInstant = instant.minusSeconds(12 * 3600)
        val endInstant = instant.plusSeconds(12 * 3600)
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        return Yoga(
            index = index,
            nameEn = names.first,
            nameHi = names.second,
            nameGu = names.third,
            startIso = startInstant.atZone(zoneId).format(formatter),
            endIso = endInstant.atZone(zoneId).format(formatter)
        )
    }

    /**
     * Compute Karana for given instant.
     */
    fun computeKarana(instant: Instant): Karana {
        val elongation = getMoonSunElongation(instant)
        val karanaIndex = (floor(elongation / 6.0).toInt() % 60) + 1

        val (nameTriple, isFixed) = when (karanaIndex) {
            1 -> Pair(KARANA_NAMES[10], true) // Kintughna
            58 -> Pair(KARANA_NAMES[7], true) // Shakuni
            59 -> Pair(KARANA_NAMES[8], true) // Chatushpada
            60 -> Pair(KARANA_NAMES[9], true) // Naga
            else -> {
                val repeatingIdx = (karanaIndex - 2) % 7
                Pair(KARANA_NAMES[repeatingIdx], false)
            }
        }

        return Karana(
            index = karanaIndex,
            nameEn = nameTriple.first,
            nameHi = nameTriple.second,
            nameGu = nameTriple.third,
            isFixed = isFixed
        )
    }

    /**
     * Compute Vara (Solar day of week from sunrise).
     */
    fun computeVara(sunriseZdt: ZonedDateTime): Vara {
        return Vara.fromDayOfWeek(sunriseZdt.dayOfWeek.value)
    }

    /**
     * Compute Vikram Samvat and Veer Nirvan Samvat.
     */
    fun computeSamvat(date: LocalDate, month: JainMonth): Pair<Int, Int> {
        // Vikram Samvat is Gregorian Year + 56 (or + 57 after Diwali / Kartika Shukla 1)
        val vs = if (month.index in 1..4) {
            date.year + 57
        } else {
            if (date.monthValue >= 11) date.year + 57 else date.year + 56
        }
        val vns = vs + 470
        return Pair(vs, vns)
    }

    /**
     * Estimate Jain Month from Sun's sidereal position (Sankranti / Rashi).
     */
    fun computeJainMonth(instant: Instant, ayanamsha: Ayanamsha): Pair<JainMonth, Boolean> {
        val jd = MeeusAstronomy.toJulianDay(instant)
        val sun = MeeusAstronomy.getSunCoordinates(jd)
        val ayanDeg = AyanamshaCalculator.calculateAyanamsha(jd, ayanamsha)
        val siderealSun = AyanamshaCalculator.toSidereal(sun.eclipticLongitude, ayanDeg)

        // Rashi (Zodiac sign): 0=Mesha (Aries), 1=Vrishabha, ...
        val rashi = floor(siderealSun / 30.0).toInt() % 12

        // Mapping from Sun's Rashi to Jain Month:
        // Mesha (0) -> Vaishakha, Vrishabha (1) -> Jyeshtha, Mithuna (2) -> Ashadha,
        // Karka (3) -> Shravana, Simha (4) -> Bhadrapada, Kanya (5) -> Ashvina,
        // Tula (6) -> Kartika, Vrishchika (7) -> Margashirsha, Dhanu (8) -> Pausha,
        // Makara (9) -> Magha, Kumbha (10) -> Phalguna, Meena (11) -> Chaitra
        val month = when (rashi) {
            6 -> JainMonth.KARTIKA
            7 -> JainMonth.MARGASHIRSHA
            8 -> JainMonth.PAUSHA
            9 -> JainMonth.MAGHA
            10 -> JainMonth.PHALGUNA
            11 -> JainMonth.CHAITRA
            0 -> JainMonth.VAISHAKHA
            1 -> JainMonth.JYESHTHA
            2 -> JainMonth.ASHADHA
            3 -> JainMonth.SHRAVANA
            4 -> JainMonth.BHADRAPADA
            5 -> JainMonth.ASHVINA
            else -> JainMonth.KARTIKA
        }

        return Pair(month, false)
    }
}
