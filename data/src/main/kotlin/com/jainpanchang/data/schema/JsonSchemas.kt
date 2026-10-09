package com.jainpanchang.data.schema

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalizedText(
    val en: String,
    val hi: String,
    val gu: String
)

@Serializable
data class KalyanakFileEntry(
    val type: String,
    @SerialName("jain_month") val jainMonth: String,
    val paksha: String,
    val tithi: Int,
    @SerialName("needs_review") val needsReview: Boolean = false,
    val notes: String = ""
)

@Serializable
data class TirthankarEntry(
    val id: Int,
    val name: LocalizedText,
    val symbol: String,
    val kalyanaks: List<KalyanakFileEntry>
)

@Serializable
data class KalyanaksData(
    val version: Int,
    val tirthankars: List<TirthankarEntry>
)

@Serializable
data class FestivalFileEntry(
    val id: String,
    val name: LocalizedText,
    val category: String,
    @SerialName("jain_month") val jainMonth: String,
    val paksha: String,
    val tithi: Int,
    val sampradays: List<String> = emptyList(),
    val description: String = "",
    @SerialName("needs_review") val needsReview: Boolean = false
)

@Serializable
data class FestivalsData(
    val version: Int,
    val festivals: List<FestivalFileEntry>
)

@Serializable
data class SampradayOverrideFile(
    @SerialName("sampraday_id") val sampradayId: String,
    val name: LocalizedText,
    @SerialName("tithi_decision_rule") val tithiDecisionRule: String,
    @SerialName("paryushan_days") val paryushanDays: Int = 8,
    @SerialName("needs_review") val needsReview: Boolean = false,
    val notes: String = ""
)

@Serializable
data class PachkhanRuleFileEntry(
    val id: String,
    val name: LocalizedText,
    @SerialName("formula_type") val formulaType: String,
    @SerialName("offset_minutes") val offsetMinutes: Int = 0,
    @SerialName("fraction_numerator") val fractionNumerator: Int = 0,
    @SerialName("fraction_denominator") val fractionDenominator: Int = 1,
    val description: LocalizedText? = null,
    @SerialName("needs_review") val needsReview: Boolean = false
)

@Serializable
data class PachkhanRulesData(
    val version: Int,
    val pachkhans: List<PachkhanRuleFileEntry>
)

@Serializable
data class NiyamCategoryEntry(
    val id: String,
    val name: LocalizedText,
    val icon: String
)

@Serializable
data class ChaudahNiyamEntry(
    val id: Int,
    val name: LocalizedText,
    val description: LocalizedText,
    @SerialName("default_maryada") val defaultMaryada: String
)

@Serializable
data class NiyamSuggestionEntry(
    val id: String,
    val category: String,
    val title: LocalizedText,
    val detail: LocalizedText
)

@Serializable
data class NiyamData(
    val version: Int,
    val categories: List<NiyamCategoryEntry>,
    @SerialName("chaudah_niyam") val chaudahNiyam: List<ChaudahNiyamEntry>,
    val suggestions: List<NiyamSuggestionEntry>
)

@Serializable
data class QuoteFileEntry(
    val id: String,
    val text: LocalizedText,
    val source: String,
    val theme: String
)

@Serializable
data class QuotesData(
    val version: Int,
    val quotes: List<QuoteFileEntry>
)

@Serializable
data class CityFileEntry(
    val name: String,
    val state: String = "",
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val timezoneId: String
)

@Serializable
data class CitiesData(
    val version: Int,
    val cities: List<CityFileEntry>
)
