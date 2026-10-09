package com.jainpanchang.engine.model

import kotlinx.serialization.Serializable

@Serializable
enum class JainMonth(
    val index: Int,
    val nameEn: String,
    val nameHi: String,
    val nameGu: String
) {
    KARTIKA(1, "Kartika", "कार्तिक", "કારતક"),
    MARGASHIRSHA(2, "Margashirsha", "मार्गशीर्ष", "માગશર"),
    PAUSHA(3, "Pausha", "पौष", "પોષ"),
    MAGHA(4, "Magha", "माघ", "મહા"),
    PHALGUNA(5, "Phalguna", "फाल्गुन", "ફાગણ"),
    CHAITRA(6, "Chaitra", "चैत्र", "ચૈત્ર"),
    VAISHAKHA(7, "Vaishakha", "वैशाख", "વૈશાખ"),
    JYESHTHA(8, "Jyeshtha", "ज्येष्ठ", "જેઠ"),
    ASHADHA(9, "Ashadha", "आषाढ़", "અષાઢ"),
    SHRAVANA(10, "Shravana", "श्रावण", "શ્રાવણ"),
    BHADRAPADA(11, "Bhadrapada", "भाद्रपद", "ભાદરવો"),
    ASHVINA(12, "Ashvina", "आश्विन", "આસો"),
    ADHIKA(13, "Adhika Month", "अधिक मास", "અધિક માસ");

    companion object {
        fun fromIndex(index: Int): JainMonth = entries.firstOrNull { it.index == index } ?: KARTIKA
    }
}

@Serializable
enum class Paksha(
    val nameEn: String,
    val nameHi: String,
    val nameGu: String
) {
    SHUKLA("Shukla Paksha (Sud)", "शुक्ल पक्ष (सुद)", "શુક્લ પક્ષ (સુદ)"),
    KRISHNA("Krishna Paksha (Vad)", "कृष्ण पक्ष (वद)", "કૃષ્ણ પક્ષ (વદ)")
}
