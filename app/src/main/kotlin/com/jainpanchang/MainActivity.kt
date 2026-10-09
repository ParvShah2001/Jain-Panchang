package com.jainpanchang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jainpanchang.ui.calendar.CalendarScreen
import com.jainpanchang.ui.calendar.CalendarViewModel
import com.jainpanchang.ui.city.CityPickerDialog
import com.jainpanchang.ui.events.MyEventsScreen
import com.jainpanchang.ui.festivals.FestivalsScreen
import com.jainpanchang.ui.home.HomeScreen
import com.jainpanchang.ui.home.HomeViewModel
import com.jainpanchang.ui.niyam.NiyamScreen
import com.jainpanchang.ui.quotes.QuotesScreen
import com.jainpanchang.ui.settings.SettingsScreen
import com.jainpanchang.ui.theme.JainPanchangTheme
import com.jainpanchang.ui.timings.TimingsScreen

enum class MainNavDestination(
    val titleGu: String,
    val titleEn: String,
    val icon: ImageVector
) {
    HOME("આજનું", "Home", Icons.Default.Home),
    CALENDAR("કેલેન્ડર", "Calendar", Icons.Default.CalendarMonth),
    TIMINGS("ચોઘડિયા", "Timings", Icons.Default.AccessTime),
    FESTIVALS("પર્વ", "Festivals", Icons.Default.Celebration),
    NIYAM("નિયમ", "Niyam", Icons.Default.SelfImprovement),
    QUOTES("સુવિચાર", "Quotes", Icons.Default.FormatQuote),
    EVENTS("પ્રસંગો", "Events", Icons.Default.Event),
    SETTINGS("સેટિંગ્સ", "Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {

    private val appContainer by lazy {
        (application as JainPanchangApplication).appContainer
    }

    private val homeViewModel: HomeViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(
                    appContainer.panchangRepository,
                    appContainer.settingsRepository
                ) as T
            }
        }
    }

    private val calendarViewModel: CalendarViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CalendarViewModel(
                    appContainer.panchangRepository,
                    appContainer.settingsRepository
                ) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val settings by appContainer.settingsRepository.settingsFlow.collectAsState(initial = null)
            val isDark = when (settings?.themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            JainPanchangTheme(darkTheme = isDark) {
                var currentDest by remember { mutableStateOf(MainNavDestination.HOME) }
                var showCityPicker by remember { mutableStateOf(false) }

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            MainNavDestination.entries.take(5).forEach { dest ->
                                NavigationBarItem(
                                    selected = currentDest == dest,
                                    onClick = { currentDest = dest },
                                    icon = { Icon(dest.icon, contentDescription = dest.titleGu) },
                                    label = { Text(dest.titleGu) }
                                )
                            }
                            // More dropdown or direct item
                            NavigationBarItem(
                                selected = currentDest == MainNavDestination.SETTINGS,
                                onClick = { currentDest = MainNavDestination.SETTINGS },
                                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                label = { Text("સેટિંગ્સ") }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentDest) {
                            MainNavDestination.HOME -> HomeScreen(
                                viewModel = homeViewModel,
                                onOpenCityPicker = { showCityPicker = true }
                            )
                            MainNavDestination.CALENDAR -> CalendarScreen(
                                viewModel = calendarViewModel
                            )
                            MainNavDestination.TIMINGS -> TimingsScreen(
                                homeViewModel = homeViewModel
                            )
                            MainNavDestination.FESTIVALS -> FestivalsScreen(
                                panchangRepository = appContainer.panchangRepository,
                                settingsRepository = appContainer.settingsRepository
                            )
                            MainNavDestination.NIYAM -> NiyamScreen(
                                niyamRepository = appContainer.niyamRepository
                            )
                            MainNavDestination.QUOTES -> QuotesScreen(
                                quotesRepository = appContainer.quotesRepository
                            )
                            MainNavDestination.EVENTS -> MyEventsScreen(
                                eventsRepository = appContainer.eventsRepository,
                                settingsRepository = appContainer.settingsRepository
                            )
                            MainNavDestination.SETTINGS -> SettingsScreen(
                                settingsRepository = appContainer.settingsRepository,
                                onOpenCityPicker = { showCityPicker = true }
                            )
                        }
                    }

                    if (showCityPicker) {
                        CityPickerDialog(
                            cityRepository = appContainer.cityRepository,
                            settingsRepository = appContainer.settingsRepository,
                            onDismiss = { showCityPicker = false }
                        )
                    }
                }
            }
        }
    }
}
