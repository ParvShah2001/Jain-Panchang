package com.jainpanchang.di

import android.content.Context
import com.jainpanchang.data.assets.AssetDataLoader
import com.jainpanchang.data.db.AppDatabase
import com.jainpanchang.data.repository.CityRepository
import com.jainpanchang.data.repository.EventsRepository
import com.jainpanchang.data.repository.NiyamRepository
import com.jainpanchang.data.repository.PanchangRepository
import com.jainpanchang.data.repository.QuotesRepository
import com.jainpanchang.data.repository.SettingsRepository
import com.jainpanchang.engine.PanchangEngine

interface AppContainer {
    val database: AppDatabase
    val assetDataLoader: AssetDataLoader
    val panchangEngine: PanchangEngine
    val settingsRepository: SettingsRepository
    val cityRepository: CityRepository
    val panchangRepository: PanchangRepository
    val niyamRepository: NiyamRepository
    val quotesRepository: QuotesRepository
    val eventsRepository: EventsRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    override val assetDataLoader: AssetDataLoader by lazy {
        AssetDataLoader(context)
    }

    override val panchangEngine: PanchangEngine by lazy {
        PanchangEngine()
    }

    override val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(context)
    }

    override val cityRepository: CityRepository by lazy {
        CityRepository(assetDataLoader, database)
    }

    override val panchangRepository: PanchangRepository by lazy {
        PanchangRepository(panchangEngine, assetDataLoader)
    }

    override val niyamRepository: NiyamRepository by lazy {
        NiyamRepository(assetDataLoader, database)
    }

    override val quotesRepository: QuotesRepository by lazy {
        QuotesRepository(assetDataLoader)
    }

    override val eventsRepository: EventsRepository by lazy {
        EventsRepository(database, panchangRepository)
    }
}
