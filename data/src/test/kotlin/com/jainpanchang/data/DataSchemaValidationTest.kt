package com.jainpanchang.data

import com.jainpanchang.data.schema.*
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.time.ZoneId

class DataSchemaValidationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private fun getAssetsDir(): File {
        val candidates = listOf(
            File("src/main/assets/data"),
            File("data/src/main/assets/data"),
            File("../data/src/main/assets/data"),
            File("app/src/main/assets/data"),
            File("../app/src/main/assets/data")
        )
        return candidates.firstOrNull { it.exists() && it.isDirectory }
            ?: throw IllegalStateException("Could not find assets/data directory in: ${candidates.map { it.absolutePath }}")
    }

    @Test
    fun testValidateKalyanaksJson() {
        val file = File(getAssetsDir(), "kalyanaks.json")
        assertTrue("kalyanaks.json exists", file.exists())

        val data = json.decodeFromString<KalyanaksData>(file.readText())
        assertEquals("Must have 24 Tirthankars", 24, data.tirthankars.size)

        val reviewList = mutableListOf<String>()

        for (t in data.tirthankars) {
            assertTrue("Tirthankar ID in 1..24", t.id in 1..24)
            assertTrue("English name non-empty", t.name.en.isNotBlank())
            assertTrue("Hindi name non-empty", t.name.hi.isNotBlank())
            assertTrue("Gujarati name non-empty", t.name.gu.isNotBlank())
            assertTrue("Symbol non-empty", t.symbol.isNotBlank())
            assertEquals("Each Tirthankar must have 5 Kalyanaks", 5, t.kalyanaks.size)

            val types = t.kalyanaks.map { it.type }.toSet()
            assertTrue("Must contain CHYAVAN", types.contains("CHYAVAN"))
            assertTrue("Must contain JANMA", types.contains("JANMA"))
            assertTrue("Must contain DIKSHA", types.contains("DIKSHA"))
            assertTrue("Must contain KEVALGYAN", types.contains("KEVALGYAN"))
            assertTrue("Must contain MOKSHA", types.contains("MOKSHA"))

            for (k in t.kalyanaks) {
                assertTrue("Tithi in 1..15", k.tithi in 1..15)
                assertTrue("Valid paksha", k.paksha in listOf("SHUKLA", "KRISHNA"))
                if (k.needsReview) {
                    reviewList.add("${t.name.en} - ${k.type}")
                }
            }
        }

        println("Kalyanaks flagged for scholar review: ${reviewList.size}")
    }

    @Test
    fun testValidateFestivalsJson() {
        val file = File(getAssetsDir(), "festivals.json")
        assertTrue("festivals.json exists", file.exists())

        val data = json.decodeFromString<FestivalsData>(file.readText())
        assertTrue("Must have festivals", data.festivals.isNotEmpty())

        val reviewList = mutableListOf<String>()
        val ids = mutableSetOf<String>()

        for (f in data.festivals) {
            assertTrue("Unique festival id", ids.add(f.id))
            assertTrue("English name non-empty", f.name.en.isNotBlank())
            assertTrue("Hindi name non-empty", f.name.hi.isNotBlank())
            assertTrue("Gujarati name non-empty", f.name.gu.isNotBlank())
            assertTrue("Tithi in 1..15", f.tithi in 1..15)
            assertTrue("Valid paksha", f.paksha in listOf("SHUKLA", "KRISHNA"))
            if (f.needsReview) {
                reviewList.add(f.id)
            }
        }

        println("Festivals flagged for scholar review: $reviewList")
    }

    @Test
    fun testValidateSampradayOverrides() {
        val dir = File(getAssetsDir(), "sampraday_overrides")
        assertTrue("sampraday_overrides directory exists", dir.exists() && dir.isDirectory)

        val files = dir.listFiles { f -> f.extension == "json" } ?: emptyArray()
        assertTrue("Must contain override files", files.isNotEmpty())

        val expectedSampradays = setOf("tapagaccha", "kharatargaccha", "sthanakvasi", "terapanth", "digambar")
        val foundSampradays = mutableSetOf<String>()

        for (file in files) {
            val data = json.decodeFromString<SampradayOverrideFile>(file.readText())
            foundSampradays.add(data.sampradayId)
            assertTrue("Name en non-empty", data.name.en.isNotBlank())
            assertTrue("Name hi non-empty", data.name.hi.isNotBlank())
            assertTrue("Name gu non-empty", data.name.gu.isNotBlank())
            assertTrue("Paryushan days valid", data.paryushanDays in listOf(8, 10))
        }

        assertTrue("All expected sampradays found", foundSampradays.containsAll(expectedSampradays))
    }

    @Test
    fun testValidatePachkhanRulesJson() {
        val file = File(getAssetsDir(), "pachkhan_rules.json")
        assertTrue("pachkhan_rules.json exists", file.exists())

        val data = json.decodeFromString<PachkhanRulesData>(file.readText())
        assertTrue("Must have pachkhan rules", data.pachkhans.isNotEmpty())

        val ids = data.pachkhans.map { it.id }.toSet()
        val expected = listOf("navkarshi", "porsi", "sadh_porsi", "purimuddh", "avaddh", "chauvihar")
        for (exp in expected) {
            assertTrue("Must contain $exp", ids.contains(exp))
        }

        for (p in data.pachkhans) {
            assertTrue("Name en non-empty", p.name.en.isNotBlank())
            assertTrue("Name hi non-empty", p.name.hi.isNotBlank())
            assertTrue("Name gu non-empty", p.name.gu.isNotBlank())
            assertTrue("Valid formula type", p.formulaType in listOf("SUNRISE_OFFSET_MINUTES", "DINMAN_FRACTION", "EXACT_SUNSET", "GUIDANCE"))
        }
    }

    @Test
    fun testValidateNiyamJson() {
        val file = File(getAssetsDir(), "niyam.json")
        assertTrue("niyam.json exists", file.exists())

        val data = json.decodeFromString<NiyamData>(file.readText())
        assertTrue("Must have categories", data.categories.isNotEmpty())
        assertEquals("Must have 14 Chaudah Niyams", 14, data.chaudahNiyam.size)
        assertTrue("Must have suggestions", data.suggestions.isNotEmpty())

        for (c in data.chaudahNiyam) {
            assertTrue("Id in 1..14", c.id in 1..14)
            assertTrue("Name non-empty", c.name.en.isNotBlank())
            assertTrue("Description non-empty", c.description.en.isNotBlank())
        }

        for (s in data.suggestions) {
            assertTrue("Title non-empty", s.title.en.isNotBlank())
            assertTrue("Detail non-empty", s.detail.en.isNotBlank())
        }
    }

    @Test
    fun testValidateQuotesJson() {
        val file = File(getAssetsDir(), "quotes.json")
        assertTrue("quotes.json exists", file.exists())

        val data = json.decodeFromString<QuotesData>(file.readText())
        assertTrue("Must have quotes", data.quotes.isNotEmpty())

        for (q in data.quotes) {
            assertTrue("English text non-empty", q.text.en.isNotBlank())
            assertTrue("Hindi text non-empty", q.text.hi.isNotBlank())
            assertTrue("Gujarati text non-empty", q.text.gu.isNotBlank())
            assertTrue("Source attribution non-empty", q.source.isNotBlank())
        }
    }

    @Test
    fun testValidateCitiesJson() {
        val file = File(getAssetsDir(), "cities.json")
        assertTrue("cities.json exists", file.exists())

        val data = json.decodeFromString<CitiesData>(file.readText())
        assertTrue("Must have cities", data.cities.size >= 30)

        for (city in data.cities) {
            assertTrue("City name non-empty", city.name.isNotBlank())
            assertTrue("Country non-empty", city.country.isNotBlank())
            assertTrue("Latitude valid: ${city.name}", city.latitude in -90.0..90.0)
            assertTrue("Longitude valid: ${city.name}", city.longitude in -180.0..180.0)
            assertNotNull("Timezone valid: ${city.timezoneId}", ZoneId.of(city.timezoneId))
        }
    }
}
