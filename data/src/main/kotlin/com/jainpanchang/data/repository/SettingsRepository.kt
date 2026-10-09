package com.jainpanchang.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.jainpanchang.engine.model.Ayanamsha
import com.jainpanchang.engine.model.GeoLocation
import com.jainpanchang.engine.model.Sampraday
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "jain_panchang_settings")

data class UserSettings(
    val selectedCity: GeoLocation,
    val sampraday: Sampraday,
    val ayanamsha: Ayanamsha,
    val themeMode: String, // "SYSTEM", "LIGHT", "DARK"
    val languageCode: String, // "SYSTEM", "EN", "HI", "GU"
    val remindTithi: Boolean,
    val remindChauvihar: Boolean,
    val remindPachkhan: Boolean,
    val remindFestivals: Boolean,
    val remindMyEvents: Boolean
)

class SettingsRepository(private val context: Context) {

    private val KEY_CITY_NAME = stringPreferencesKey("city_name")
    private val KEY_CITY_LAT = doublePreferencesKey("city_lat")
    private val KEY_CITY_LNG = doublePreferencesKey("city_lng")
    private val KEY_CITY_TZ = stringPreferencesKey("city_tz")

    private val KEY_SAMPRADAY = stringPreferencesKey("sampraday")
    private val KEY_AYANAMSHA = stringPreferencesKey("ayanamsha")
    private val KEY_THEME = stringPreferencesKey("theme_mode")
    private val KEY_LANGUAGE = stringPreferencesKey("language_code")

    private val KEY_REMIND_TITHI = booleanPreferencesKey("remind_tithi")
    private val KEY_REMIND_CHAUVIHAR = booleanPreferencesKey("remind_chauvihar")
    private val KEY_REMIND_PACHKHAN = booleanPreferencesKey("remind_pachkhan")
    private val KEY_REMIND_FESTIVALS = booleanPreferencesKey("remind_festivals")
    private val KEY_REMIND_MY_EVENTS = booleanPreferencesKey("remind_my_events")

    val settingsFlow: Flow<UserSettings> = context.dataStore.data.map { pref ->
        val cityName = pref[KEY_CITY_NAME] ?: "Mumbai"
        val cityLat = pref[KEY_CITY_LAT] ?: 18.9220
        val cityLng = pref[KEY_CITY_LNG] ?: 72.8347
        val cityTz = pref[KEY_CITY_TZ] ?: "Asia/Kolkata"

        val sampradayId = pref[KEY_SAMPRADAY] ?: "tapagaccha"
        val ayanamshaName = pref[KEY_AYANAMSHA] ?: "LAHIRI"

        UserSettings(
            selectedCity = GeoLocation(cityName, cityLat, cityLng, 0.0, cityTz),
            sampraday = Sampraday.fromId(sampradayId),
            ayanamsha = try { Ayanamsha.valueOf(ayanamshaName) } catch (e: Exception) { Ayanamsha.LAHIRI },
            themeMode = pref[KEY_THEME] ?: "SYSTEM",
            languageCode = pref[KEY_LANGUAGE] ?: "SYSTEM",
            remindTithi = pref[KEY_REMIND_TITHI] ?: true,
            remindChauvihar = pref[KEY_REMIND_CHAUVIHAR] ?: true,
            remindPachkhan = pref[KEY_REMIND_PACHKHAN] ?: false,
            remindFestivals = pref[KEY_REMIND_FESTIVALS] ?: true,
            remindMyEvents = pref[KEY_REMIND_MY_EVENTS] ?: true
        )
    }

    suspend fun setSelectedCity(city: GeoLocation) {
        context.dataStore.edit { pref ->
            pref[KEY_CITY_NAME] = city.name
            pref[KEY_CITY_LAT] = city.latitude
            pref[KEY_CITY_LNG] = city.longitude
            pref[KEY_CITY_TZ] = city.timezoneId
        }
    }

    suspend fun setSampraday(sampraday: Sampraday) {
        context.dataStore.edit { pref ->
            pref[KEY_SAMPRADAY] = sampraday.id
        }
    }

    suspend fun setAyanamsha(ayanamsha: Ayanamsha) {
        context.dataStore.edit { pref ->
            pref[KEY_AYANAMSHA] = ayanamsha.name
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { pref ->
            pref[KEY_THEME] = mode
        }
    }

    suspend fun setLanguageCode(code: String) {
        context.dataStore.edit { pref ->
            pref[KEY_LANGUAGE] = code
        }
    }

    suspend fun setNotificationToggle(key: String, enabled: Boolean) {
        context.dataStore.edit { pref ->
            when (key) {
                "tithi" -> pref[KEY_REMIND_TITHI] = enabled
                "chauvihar" -> pref[KEY_REMIND_CHAUVIHAR] = enabled
                "pachkhan" -> pref[KEY_REMIND_PACHKHAN] = enabled
                "festivals" -> pref[KEY_REMIND_FESTIVALS] = enabled
                "my_events" -> pref[KEY_REMIND_MY_EVENTS] = enabled
            }
        }
    }
}
