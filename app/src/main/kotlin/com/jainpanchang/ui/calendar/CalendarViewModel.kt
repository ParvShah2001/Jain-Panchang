package com.jainpanchang.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jainpanchang.data.repository.PanchangRepository
import com.jainpanchang.data.repository.SettingsRepository
import com.jainpanchang.data.repository.UserSettings
import com.jainpanchang.engine.model.DailyPanchang
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

data class CalendarUiState(
    val selectedYearMonth: YearMonth = YearMonth.now(),
    val monthPanchangList: List<DailyPanchang> = emptyList(),
    val selectedDayPanchang: DailyPanchang? = null,
    val userSettings: UserSettings? = null,
    val isLoading: Boolean = false
)

class CalendarViewModel(
    private val panchangRepository: PanchangRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settingsFlow.collect { settings ->
                _uiState.update { it.copy(userSettings = settings) }
                loadMonth(YearMonth.now(), settings)
            }
        }
    }

    fun nextMonth() {
        val next = _uiState.value.selectedYearMonth.plusMonths(1)
        val s = _uiState.value.userSettings ?: return
        loadMonth(next, s)
    }

    fun prevMonth() {
        val prev = _uiState.value.selectedYearMonth.minusMonths(1)
        val s = _uiState.value.userSettings ?: return
        loadMonth(prev, s)
    }

    fun jumpToYearMonth(year: Int, month: Int) {
        val ym = YearMonth.of(year, month)
        val s = _uiState.value.userSettings ?: return
        loadMonth(ym, s)
    }

    fun selectDay(dayPanchang: DailyPanchang) {
        _uiState.update { it.copy(selectedDayPanchang = dayPanchang) }
    }

    fun clearSelectedDay() {
        _uiState.update { it.copy(selectedDayPanchang = null) }
    }

    private fun loadMonth(ym: YearMonth, settings: UserSettings) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedYearMonth = ym) }
            val list = panchangRepository.getMonthPanchang(
                year = ym.year,
                month = ym.monthValue,
                location = settings.selectedCity,
                ayanamsha = settings.ayanamsha
            )
            val today = LocalDate.now()
            val initialSelected = list.firstOrNull { it.dateIso == today.toString() } ?: list.firstOrNull()

            _uiState.update {
                it.copy(
                    monthPanchangList = list,
                    selectedDayPanchang = initialSelected,
                    isLoading = false
                )
            }
        }
    }
}
