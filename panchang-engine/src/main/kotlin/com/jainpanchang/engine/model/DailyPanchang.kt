package com.jainpanchang.engine.model

import kotlinx.serialization.Serializable

@Serializable
enum class Sampraday(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val nameGu: String
) {
    SHWETAMBAR_MURTIPUJAK_TAPA("tapagaccha", "Shwetambar Murtipujak (Tapagaccha)", "श्वेतांबर मूर्तिपूजक (तपागच्छ)", "શ્વેતાંબર મૂર્તિપૂજક (તપાગચ્છ)"),
    SHWETAMBAR_MURTIPUJAK_KHARATARA("kharatargaccha", "Shwetambar Murtipujak (Kharatargaccha)", "श्वेतांबर मूर्तिपूजक (खरतरगच्छ)", "શ્વેતાંબર મૂર્તિપૂજક (ખરતરગચ્છ)"),
    SHWETAMBAR_STHANAKVASI("sthanakvasi", "Shwetambar Sthanakvasi", "श्वेतांबर स्थानकवासी", "શ્વેતાંબર સ્થાનકવાસી"),
    SHWETAMBAR_TERAPANTH("terapanth", "Shwetambar Terapanth", "श्वेतांबर तेरापंथ", "શ્વેતાંબર તેરાપંથ"),
    DIGAMBAR("digambar", "Digambar", "दिगंबर", "દિગંબર");

    companion object {
        fun fromId(id: String): Sampraday = entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: SHWETAMBAR_MURTIPUJAK_TAPA
    }
}

@Serializable
enum class KalyanakType(
    val nameEn: String,
    val nameHi: String,
    val nameGu: String
) {
    CHYAVAN("Chyavan (Conception)", "च्यवन कल्याणक", "ચ્યવન કલ્યાણક"),
    JANMA("Janma (Birth)", "जन्म कल्याणक", "જન્મ કલ્યાણક"),
    DIKSHA("Diksha (Renunciation)", "दीक्षा कल्याणक", "દીક્ષા કલ્યાણક"),
    KEVALGYAN("Kevalgyan (Omniscience)", "केवलज्ञान कल्याणक", "કેવળજ્ઞાન કલ્યાણક"),
    MOKSHA("Moksha (Nirvana)", "मोक्ष कल्याणक", "મોક્ષ કલ્યાણક")
}

@Serializable
data class Kalyanak(
    val tirthankarId: Int, // 1 to 24
    val tirthankarNameEn: String,
    val tirthankarNameHi: String,
    val tirthankarNameGu: String,
    val type: KalyanakType,
    val jainMonth: JainMonth,
    val paksha: Paksha,
    val tithi: Int,
    val needsReview: Boolean = false,
    val notes: String = ""
)

@Serializable
data class Festival(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val nameGu: String,
    val jainMonth: JainMonth,
    val paksha: Paksha,
    val tithi: Int,
    val category: String, // e.g. "PARYUSHAN", "MAJOR_PARV", "OLI", "CHATH"
    val sampradays: List<String> = emptyList(), // empty means all
    val description: String = "",
    val needsReview: Boolean = false
)

@Serializable
data class DailyPanchang(
    val dateIso: String, // YYYY-MM-DD
    val location: GeoLocation,
    val solarTimes: SolarTimes,
    val lunarTimes: LunarTimes,
    val tithi: Tithi,
    val nextTithi: Tithi? = null,
    val nakshatra: Nakshatra,
    val yoga: Yoga,
    val karana: Karana,
    val vara: Vara,
    val jainMonth: JainMonth,
    val isAdhikaMonth: Boolean = false,
    val vikramSamvat: Int,
    val veerNirvanSamvat: Int,
    val dayChoghadiya: List<ChoghadiyaSlot>,
    val nightChoghadiya: List<ChoghadiyaSlot>,
    val horas: List<HoraSlot>,
    val gowriDay: List<GowriSlot>,
    val gowriNight: List<GowriSlot>,
    val muhurats: List<MuhuratPeriod>,
    val pachkhans: List<PachkhanTiming>,
    val kalyanaksToday: List<Kalyanak> = emptyList(),
    val festivalsToday: List<Festival> = emptyList()
)
