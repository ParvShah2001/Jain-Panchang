package com.jainpanchang.engine.model

import kotlinx.serialization.Serializable

@Serializable
enum class Ayanamsha(val displayName: String) {
    LAHIRI("Lahiri (Chitrapaksha)"),
    RAMAN("B.V. Raman"),
    KRISHNAMURTI("KP (Krishnamurti)"),
    SAYANA("Sayana (Tropical)")
}

@Serializable
data class Tithi(
    val number: Int, // 1 to 15
    val paksha: Paksha,
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val startIso: String,
    val endIso: String,
    val isKshaya: Boolean = false,
    val isVriddhi: Boolean = false
) {
    val fullDisplayNameEn: String get() = "${paksha.nameEn} $nameEn"
    val fullDisplayNameHi: String get() = "${paksha.nameHi} $nameHi"
    val fullDisplayNameGu: String get() = "${paksha.nameGu} $nameGu"
}

@Serializable
data class Nakshatra(
    val index: Int, // 1 to 27
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val startIso: String,
    val endIso: String
)

@Serializable
data class Yoga(
    val index: Int, // 1 to 27
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val startIso: String,
    val endIso: String
)

@Serializable
data class Karana(
    val index: Int, // 1 to 60
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val isFixed: Boolean
)

@Serializable
enum class Vara(
    val dayOfWeek: Int, // 1 = Monday .. 7 = Sunday
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val rulerPlanet: String
) {
    RAVIVARA(7, "Ravivara (Sunday)", "रविवार", "રવિવાર", "Sun"),
    SOMAVARA(1, "Somavara (Monday)", "सोमवार", "સોમવાર", "Moon"),
    MANGALAVARA(2, "Mangalavara (Tuesday)", "मंगलवार", "મંગળવાર", "Mars"),
    BUDHAVARA(3, "Budhavara (Wednesday)", "बुधवार", "બુધવાર", "Mercury"),
    GURUVARA(4, "Guruvara (Thursday)", "गुरुवार", "ગુરુવાર", "Jupiter"),
    SHUKRAVARA(5, "Shukravara (Friday)", "शुक्रवार", "શુક્રવાર", "Venus"),
    SHANIVARA(6, "Shanivara (Saturday)", "शनिवार", "શનિવાર", "Saturn");

    companion object {
        fun fromDayOfWeek(dow: Int): Vara = entries.firstOrNull { it.dayOfWeek == dow } ?: RAVIVARA
    }
}
