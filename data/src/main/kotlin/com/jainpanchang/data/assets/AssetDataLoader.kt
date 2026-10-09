package com.jainpanchang.data.assets

import android.content.Context
import com.jainpanchang.data.schema.*
import com.jainpanchang.engine.model.*
import kotlinx.serialization.json.Json

class AssetDataLoader(private val context: Context) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private fun readAsset(path: String): String {
        return context.assets.open(path).bufferedReader().use { it.readText() }
    }

    fun loadCities(): List<CityFileEntry> {
        val content = readAsset("data/cities.json")
        return json.decodeFromString<CitiesData>(content).cities
    }

    fun loadKalyanaks(): List<Kalyanak> {
        val content = readAsset("data/kalyanaks.json")
        val data = json.decodeFromString<KalyanaksData>(content)
        val result = mutableListOf<Kalyanak>()

        for (t in data.tirthankars) {
            for (k in t.kalyanaks) {
                val kType = try {
                    KalyanakType.valueOf(k.type)
                } catch (e: Exception) {
                    KalyanakType.JANMA
                }
                val jMonth = try {
                    JainMonth.valueOf(k.jainMonth)
                } catch (e: Exception) {
                    JainMonth.KARTIKA
                }
                val paksha = try {
                    Paksha.valueOf(k.paksha)
                } catch (e: Exception) {
                    Paksha.SHUKLA
                }

                result.add(
                    Kalyanak(
                        tirthankarId = t.id,
                        tirthankarNameEn = t.name.en,
                        tirthankarNameHi = t.name.hi,
                        tirthankarNameGu = t.name.gu,
                        type = kType,
                        jainMonth = jMonth,
                        paksha = paksha,
                        tithi = k.tithi,
                        needsReview = k.needsReview,
                        notes = k.notes
                    )
                )
            }
        }
        return result
    }

    fun loadFestivals(): List<Festival> {
        val content = readAsset("data/festivals.json")
        val data = json.decodeFromString<FestivalsData>(content)
        return data.festivals.map { f ->
            val jMonth = try {
                JainMonth.valueOf(f.jainMonth)
            } catch (e: Exception) {
                JainMonth.KARTIKA
            }
            val paksha = try {
                Paksha.valueOf(f.paksha)
            } catch (e: Exception) {
                Paksha.SHUKLA
            }
            Festival(
                id = f.id,
                nameEn = f.name.en,
                nameHi = f.name.hi,
                nameGu = f.name.gu,
                jainMonth = jMonth,
                paksha = paksha,
                tithi = f.tithi,
                category = f.category,
                sampradays = f.sampradays,
                description = f.description,
                needsReview = f.needsReview
            )
        }
    }

    fun loadNiyamData(): NiyamData {
        val content = readAsset("data/niyam.json")
        return json.decodeFromString(content)
    }

    fun loadQuotes(): List<QuoteFileEntry> {
        val content = readAsset("data/quotes.json")
        return json.decodeFromString<QuotesData>(content).quotes
    }
}
