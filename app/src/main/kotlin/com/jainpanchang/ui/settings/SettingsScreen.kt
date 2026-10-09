package com.jainpanchang.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jainpanchang.R
import com.jainpanchang.data.repository.SettingsRepository
import com.jainpanchang.data.repository.UserSettings
import com.jainpanchang.engine.model.Ayanamsha
import com.jainpanchang.engine.model.Sampraday
import com.jainpanchang.ui.theme.AppLanguage
import com.jainpanchang.ui.theme.LocalAppLanguage
import com.jainpanchang.ui.theme.SaffronPrimary
import com.jainpanchang.ui.theme.displayName
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository,
    onOpenCityPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val settings by settingsRepository.settingsFlow.collectAsState(initial = null)
    val scope = rememberCoroutineScope()
    val appLanguage = LocalAppLanguage.current

    var showSampradayDialog by remember { mutableStateOf(false) }
    var showAyanamshaDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    val userSettings = settings ?: return

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.title_settings),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = SaffronPrimary
            )
        }

        // 1. Location Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.section_location),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenCityPicker() }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = userSettings.selectedCity.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Timezone: ${userSettings.selectedCity.timezoneId}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        }

        // 2. Tradition & Calculation Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.section_panchang_rules),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Sampraday Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSampradayDialog = true }
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = stringResource(R.string.select_sampraday), style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = userSettings.sampraday.displayName(appLanguage),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = SaffronPrimary
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }

                    HorizontalDivider()

                    // Ayanamsha Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAyanamshaDialog = true }
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = stringResource(R.string.select_ayanamsha), style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = userSettings.ayanamsha.displayName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = SaffronPrimary
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        }

        // 3. App Display, Language & Theme Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.section_appearance),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Language Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLanguageDialog = true }
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = stringResource(R.string.language), style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = appLanguage.nativeName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = SaffronPrimary
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }

                    HorizontalDivider()

                    // Theme Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showThemeDialog = true }
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = stringResource(R.string.theme), style = MaterialTheme.typography.bodyLarge)
                            val themeName = when (userSettings.themeMode) {
                                "LIGHT" -> stringResource(R.string.theme_light)
                                "DARK" -> stringResource(R.string.theme_dark)
                                else -> stringResource(R.string.theme_system)
                            }
                            Text(
                                text = themeName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = SaffronPrimary
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        }

        // 4. Notifications Toggles Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.section_notifications),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    NotificationSwitchItem(
                        title = stringResource(R.string.remind_tithi),
                        checked = userSettings.remindTithi,
                        onCheckedChange = { scope.launch { settingsRepository.setNotificationToggle("tithi", it) } }
                    )
                    NotificationSwitchItem(
                        title = stringResource(R.string.remind_chauvihar),
                        checked = userSettings.remindChauvihar,
                        onCheckedChange = { scope.launch { settingsRepository.setNotificationToggle("chauvihar", it) } }
                    )
                    NotificationSwitchItem(
                        title = stringResource(R.string.remind_pachkhan),
                        checked = userSettings.remindPachkhan,
                        onCheckedChange = { scope.launch { settingsRepository.setNotificationToggle("pachkhan", it) } }
                    )
                    NotificationSwitchItem(
                        title = stringResource(R.string.remind_my_events),
                        checked = userSettings.remindMyEvents,
                        onCheckedChange = { scope.launch { settingsRepository.setNotificationToggle("my_events", it) } }
                    )
                }
            }
        }

        // 5. Privacy Policy & Offline Guarantee Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.section_trust),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.privacy_guarantee_text),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { showPrivacyDialog = true }) {
                        Text(stringResource(R.string.read_privacy_policy))
                    }
                }
            }
        }
    }

    // Language Selection Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.select_language)) },
            text = {
                Column {
                    AppLanguage.entries.forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch { settingsRepository.setLanguageCode(lang.code) }
                                    showLanguageDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = appLanguage == lang,
                                onClick = {
                                    scope.launch { settingsRepository.setLanguageCode(lang.code) }
                                    showLanguageDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = lang.nativeName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (appLanguage == lang) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Sampraday Selection Dialog
    if (showSampradayDialog) {
        AlertDialog(
            onDismissRequest = { showSampradayDialog = false },
            title = { Text(stringResource(R.string.select_sampraday)) },
            text = {
                Column {
                    for (samp in Sampraday.entries) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch { settingsRepository.setSampraday(samp) }
                                    showSampradayDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = userSettings.sampraday == samp,
                                onClick = {
                                    scope.launch { settingsRepository.setSampraday(samp) }
                                    showSampradayDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = samp.displayName(appLanguage), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Ayanamsha Selection Dialog
    if (showAyanamshaDialog) {
        AlertDialog(
            onDismissRequest = { showAyanamshaDialog = false },
            title = { Text(stringResource(R.string.select_ayanamsha)) },
            text = {
                Column {
                    for (ayan in Ayanamsha.entries) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch { settingsRepository.setAyanamsha(ayan) }
                                    showAyanamshaDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = userSettings.ayanamsha == ayan,
                                onClick = {
                                    scope.launch { settingsRepository.setAyanamsha(ayan) }
                                    showAyanamshaDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = ayan.displayName, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Theme Selection Dialog
    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text(stringResource(R.string.theme)) },
            text = {
                Column {
                    listOf(
                        "SYSTEM" to stringResource(R.string.theme_system),
                        "LIGHT" to stringResource(R.string.theme_light),
                        "DARK" to stringResource(R.string.theme_dark)
                    ).forEach { (mode, title) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch { settingsRepository.setThemeMode(mode) }
                                    showThemeDialog = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = userSettings.themeMode == mode,
                                onClick = {
                                    scope.launch { settingsRepository.setThemeMode(mode) }
                                    showThemeDialog = false
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = title, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text(stringResource(R.string.privacy_policy)) },
            text = {
                Text(
                    "Jain Panchang is an ad-free, completely offline application dedicated to Jain religious practice. " +
                    "Zero personal data is gathered, transmitted, or sold. All astronomical and tithi calculations run locally on your device. " +
                    "Location permission (if granted) is strictly used on-device to compute local sunrise and sunset timings for your coordinates and is never shared over any network."
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) { Text(stringResource(R.string.close)) }
            }
        )
    }
}

@Composable
private fun NotificationSwitchItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
