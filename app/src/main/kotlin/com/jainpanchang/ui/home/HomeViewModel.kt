package com.jainpanchang.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainpanchang.data.repository.PanchangRepository
import com.jainpanchang.data.repository.SettingsRepository
import com.jainpanchang.data.repository.UserSettings
import com.jainpanchang.engine.model.ChoghadiyaSlot
import com.jainpanchang.engine.model.DailyPanchang
import com.jainpanchang.engine.model.PachkhanTiming
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime

data class HomeUiState(
    val panchang: DailyPanchang? = null,
    val userSettings: UserSettings? = null,
    val currentChoghadiya: ChoghadiyaSlot? = null,
    val choghadiyaCountdownMinutes: Long = 0,
    val nextTiming: Pair<String, String>? = null, // e.g. ("Navkarshi", "in 24 mins")
    val isLoading: Boolean = true
)

class HomeViewModel(
    private val panchangRepository: PanchangRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var selectedDate = LocalDate.now()

    init {
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                refreshPanchang(settings)
            }
        }

        // Live timer tick every 30 seconds for choghadiya countdown
        viewModelScope.launch {
            while (true) {
                delay(30_000)
                updateLiveTimings()
            }
        }
    }

    fun setDate(date: LocalDate) {
        selectedDate = date
        val currentSettings = _uiState.value.userSettings ?: return
        refreshPanchang(currentSettings)
    }

    private fun refreshPanchang(settings: UserSettings) {
        val panchang = panchangRepository.getDailyPanchang(
            date = selectedDate,
            location = settings.selectedCity,
            ayanamsha = settings.ayanamsha
        )
        _uiState.update {
            it.copy(
                panchang = panchang,
                userSettings = settings,
                isLoading = false
            )
        }
        updateLiveTimings()
    }

    private fun updateLiveTimings() {
        val panchang = _uiState.value.panchang ?: return
        val now = ZonedDateTime.now(panchang.location.zoneId)

        // Find current choghadiya
        val allSlots = panchang.dayChoghadiya + panchang.nightChoghadiya
        val currentSlot = allSlots.firstOrNull { slot ->
            val start = ZonedDateTime.parse(slot.startIso)
            val end = ZonedDateTime.parse(slot.endIso)
            (now.isEqual(start) || now.isAfter(start)) && now.isBefore(end)
        } ?: allSlots.firstOrNull()

        val countdownMin = if (currentSlot != null) {
            val end = ZonedDateTime.parse(currentSlot.endIso)
            val diff = Duration.between(now, end).toMinutes()
            diff.coerceAtLeast(0)
        } else 0L

        // Find next key timing
        val nextPachkhan = panchang.pachkhans.firstOrNull { p ->
            val t = ZonedDateTime.parse(p.timeIso)
            t.isAfter(now)
        }
        val nextTimingPair = if (nextPachkhan != null) {
            val t = ZonedDateTime.parse(nextPachkhan.timeIso)
            val diff = Duration.between(now, t).toMinutes()
            Pair(nextPachkhan.nameEn, if (diff <= 60) "in $diff mins" else "at ${t.toLocalTime()}")
        } else {
            Pair("Sunset / Chauvihar", "Completed for today")
        }

        _uiState.update {
            it.copy(
                currentChoghadiya = currentSlot,
                choghadiyaCountdownMinutes = countdownMin,
                nextTiming = nextTimingPair
            )
        }
    }
}
