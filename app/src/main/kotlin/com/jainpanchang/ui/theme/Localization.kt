package com.jainpanchang.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import com.jainpanchang.R
import com.jainpanchang.data.schema.LocalizedText
import com.jainpanchang.engine.model.*
import java.util.Locale

enum class AppLanguage(
    val code: String,
    val titleRes: Int,
    val locale: Locale,
    val nativeName: String
) {
    ENGLISH("EN", R.string.language_en, Locale.ENGLISH, "English"),
    HINDI("HI", R.string.language_hi, Locale.forLanguageTag("hi-IN"), "हिंदी"),
    GUJARATI("GU", R.string.language_gu, Locale.forLanguageTag("gu-IN"), "ગુજરાતી");

    companion object {
        fun fromCode(code: String): AppLanguage = when (code.uppercase()) {
            "EN" -> ENGLISH
            "HI" -> HINDI
            "GU" -> GUJARATI
            else -> GUJARATI // Default to Gujarati per typical Jain Panchang users, or configurable
        }
    }
}

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.GUJARATI }

fun LocalizedText.get(lang: AppLanguage): String = when (lang) {
    AppLanguage.ENGLISH -> en
    AppLanguage.HINDI -> hi
    AppLanguage.GUJARATI -> gu
}

fun Tithi.displayName(lang: AppLanguage): String = when (lang) {
    AppLanguage.ENGLISH -> fullDisplayNameEn
    AppLanguage.HINDI -> fullDisplayNameHi
    AppLanguage.GUJARATI -> fullDisplayNameGu
}

fun Tithi.shortName(lang: AppLanguage): String = when (lang) {
    AppLanguage.ENGLISH -> nameEn
    AppLanguage.HINDI -> nameHi
    AppLanguage.GUJARATI -> nameGu
}

fun Paksha.displayName(lang: AppLanguage): String = when (lang) {
    AppLanguage.ENGLISH -> nameEn
    AppLanguage.HINDI -> nameHi
    AppLanguage.GUJARATI -> nameGu
}

fun Nakshatra.displayName(lang: AppLanguage): String = when (lang) {
    AppLanguage.ENGLISH -> nameEn
    AppLanguage.HINDI -> nameHi
    AppLanguage.GUJARATI -> nameGu
}

fun Yoga.displayName(lang: AppLanguage): String = when (lang) {
    AppLanguage.ENGLISH -> nameEn
    AppLanguage.HINDI -> nameHi
    AppLanguage.GUJARATI -> nameGu
}

fun Karana.displayName(lang: AppLanguage): String = when (lang) {
    AppLanguage.ENGLISH -> nameEn
    AppLanguage.HINDI -> nameHi
    AppLanguage.GUJARATI -> nameGu
}

fun Vara.displayName(lang: AppLanguage): String = when (lang) {
    AppLanguage.ENGLISH -> nameEn
    AppLanguage.HINDI -> nameHi
    AppLanguage.GUJARATI -> nameGu
}

fun JainMonth.displayName(lang: AppLanguage): String = when (lang) {
    AppLanguage.ENGLISH -> nameEn
    AppLanguage.HINDI -> nameHi
    AppLanguage.GUJARATI -> nameGu
}

fun ChoghadiyaType.displayName(lang: AppLanguage): String = when (lang) {
    AppLanguage.ENGLISH -> nameEn
    AppLanguage.HINDI -> nameHi
    AppLanguage.GUJARATI -> nameGu
}

fun HoraSlot.displayName(lang: AppLanguage): String = when (lang) {
    AppLanguage.ENGLISH -> planetName
    AppLanguage.HINDI -> nameHi
    AppLanguage.GUJARATI -> nameGu
}

fun GowriSlot.displayName(lang: AppLanguage): String = when (lang) {
    AppLanguage.ENGLISH -> nameEn
    AppLanguage.HINDI -> nameHi
    AppLanguage.GUJARATI -> nameGu
}

fun MuhuratPeriod.displayName(lang: AppLanguage): String = when (lang) {
    AppLanguage.ENGLISH -> nameEn
    AppLanguage.HINDI -> nameHi
    AppLanguage.GUJARATI -> nameGu
}

fun PachkhanTiming.displayName(lang: AppLanguage): String = when (lang) {
    AppLanguage.ENGLISH -> nameEn
    AppLanguage.HINDI -> nameHi
    AppLanguage.GUJARATI -> nameGu
}

fun Kalyanak.displayName(lang: AppLanguage): String = "${tirthankarDisplayName(lang)} - ${type.displayName(lang)}"

fun Kalyanak.tirthankarDisplayName(lang: AppLanguage): String = when (lang) {
    AppLanguage.ENGLISH -> tirthankarNameEn
    AppLanguage.HINDI -> tirthankarNameHi
    AppLanguage.GUJARATI -> tirthankarNameGu
}

fun KalyanakType.displayName(lang: AppLanguage): String = when (lang) {
    AppLanguage.ENGLISH -> nameEn
    AppLanguage.HINDI -> nameHi
    AppLanguage.GUJARATI -> nameGu
}

fun Festival.displayName(lang: AppLanguage): String = when (lang) {
    AppLanguage.ENGLISH -> nameEn
    AppLanguage.HINDI -> nameHi
    AppLanguage.GUJARATI -> nameGu
}

fun Sampraday.displayName(lang: AppLanguage): String = when (lang) {
    AppLanguage.ENGLISH -> nameEn
    AppLanguage.HINDI -> nameHi
    AppLanguage.GUJARATI -> nameGu
}
