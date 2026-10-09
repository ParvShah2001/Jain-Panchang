package com.jainpanchang.data.repository

import com.jainpanchang.data.assets.AssetDataLoader
import com.jainpanchang.data.schema.QuoteFileEntry
import java.time.LocalDate

class QuotesRepository(private val assetDataLoader: AssetDataLoader) {
    private var cachedQuotes: List<QuoteFileEntry>? = null

    fun getQuotes(): List<QuoteFileEntry> {
        if (cachedQuotes == null) {
            cachedQuotes = assetDataLoader.loadQuotes()
        }
        return cachedQuotes ?: emptyList()
    }

    fun getDailyQuote(date: LocalDate): QuoteFileEntry {
        val list = getQuotes()
        if (list.isEmpty()) {
            return QuoteFileEntry(
                id = "default",
                text = com.jainpanchang.data.schema.LocalizedText("Ahimsa Paramo Dharma", "अहिंसा परमो धर्मः", "અહિંસા પરમો ધર્મ"),
                source = "Jain Agamas",
                theme = "Ahimsa"
            )
        }
        val idx = (date.dayOfYear - 1) % list.size
        return list[idx]
    }
}
