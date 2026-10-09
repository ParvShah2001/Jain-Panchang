package com.jainpanchang.engine.model

import kotlinx.serialization.Serializable

@Serializable
data class SolarTimes(
    val sunriseIso: String,
    val sunsetIso: String,
    val solarNoonIso: String,
    val dayLengthMinutes: Long,
    val nightLengthMinutes: Long
)

@Serializable
data class LunarTimes(
    val moonriseIso: String?,
    val moonsetIso: String?
)

@Serializable
enum class ChoghadiyaType(
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val nature: Nature,
    val rulerPlanet: String
) {
    AMRIT("Amrit", "अमृत", "અમૃત", Nature.BEST, "Moon"),
    SHUBH("Shubh", "शुभ", "શુભ", Nature.GOOD, "Jupiter"),
    LABH("Labh", "लाभ", "લાભ", Nature.GOOD, "Mercury"),
    CHAR("Char", "चर", "ચલ", Nature.NEUTRAL, "Venus"),
    ROG("Rog", "रोग", "રોગ", Nature.BAD, "Mars"),
    KAAL("Kaal", "काल", "કાળ", Nature.WORST, "Saturn"),
    UDVEG("Udveg", "उद्वेग", "ઉદ્વેગ", Nature.BAD, "Sun");

    enum class Nature { BEST, GOOD, NEUTRAL, BAD, WORST }
}

@Serializable
data class ChoghadiyaSlot(
    val index: Int, // 1 to 8
    val isDay: Boolean,
    val type: ChoghadiyaType,
    val startIso: String,
    val endIso: String
)

@Serializable
data class HoraSlot(
    val hourIndex: Int, // 1 to 24
    val planetName: String,
    val nameHi: String,
    val nameGu: String,
    val startIso: String,
    val endIso: String
)

@Serializable
data class GowriSlot(
    val index: Int, // 1 to 8
    val isDay: Boolean,
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val isAuspicious: Boolean,
    val startIso: String,
    val endIso: String
)

@Serializable
data class MuhuratPeriod(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val isAuspicious: Boolean,
    val startIso: String,
    val endIso: String,
    val description: String = ""
)

@Serializable
data class PachkhanTiming(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val timeIso: String,
    val formulaType: String,
    val description: String = "",
    val needsReview: Boolean = false
)
