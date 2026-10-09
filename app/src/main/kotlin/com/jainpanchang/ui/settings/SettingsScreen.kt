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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jainpanchang.data.repository.SettingsRepository
import com.jainpanchang.data.repository.UserSettings
import com.jainpanchang.engine.model.Ayanamsha
import com.jainpanchang.engine.model.Sampraday
import com.jainpanchang.ui.theme.SaffronPrimary
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

    var showSampradayDialog by remember { mutableStateOf(false) }
    var showAyanamshaDialog by remember { mutableStateOf(false) }
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
                text = "સેટિંગ્સ (Settings)",
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
                        text = "સ્થાન (Location)",
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
                        text = "પરંપરા અને ગણતરી (Tradition & Calculation)",
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
                            Text(text = "સંપ્રદાય (Tradition)", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = userSettings.sampraday.nameGu,
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
                            Text(text = "અયનાંશ (Ayanamsha)", style = MaterialTheme.typography.bodyLarge)
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

        // 3. App Display & Theme Card
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
                        text = "થીમ અને દેખાવ (Appearance)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showThemeDialog = true }
                            .padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "થીમ (Theme)", style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = userSettings.themeMode,
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
                        text = "નોટિફિકેશન અને સ્મરણ (Notifications)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    NotificationSwitchItem(
                        title = "તિથિ અને પર્વ સૂચના (Tithi & Festivals)",
                        checked = userSettings.remindTithi,
                        onCheckedChange = { scope.launch { settingsRepository.setNotificationToggle("tithi", it) } }
                    )
                    NotificationSwitchItem(
                        title = "સૂર્યાસ્ત / ચૌવિહાર સ્મરણ (Sunset / Chauvihar)",
                        checked = userSettings.remindChauvihar,
                        onCheckedChange = { scope.launch { settingsRepository.setNotificationToggle("chauvihar", it) } }
                    )
                    NotificationSwitchItem(
                        title = "પચ્ચખાણ સ્મરણ (Pachkhan Reminders)",
                        checked = userSettings.remindPachkhan,
                        onCheckedChange = { scope.launch { settingsRepository.setNotificationToggle("pachkhan", it) } }
                    )
                    NotificationSwitchItem(
                        title = "મારા પ્રસંગો સ્મરણ (My Events)",
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
                        text = "ગોપનીયતા અને અધિકાર (Privacy & Trust)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• ૧૦૦% ઓફલાઇન: કોઈ ડેટા કે લોકેશન ઉપકરણ બહાર જતું નથી.\n• કોઈ જાહેરાત નહીં (Ad-Free) અને કોઈ ટ્રેકિંગ નહીં.\n• ગણતરી સંપૂર્ણપણે ઑન-ડિવાઇસ ગાણિતિક અને ખગોળીય અલ્ગોરિધમથી થાય છે.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { showPrivacyDialog = true }) {
                        Text("સંપૂર્ણ ગોપનીયતા નીતિ વાંચો (Read Full Privacy Policy)")
                    }
                }
            }
        }
    }

    // Sampraday Selection Dialog
    if (showSampradayDialog) {
        AlertDialog(
            onDismissRequest = { showSampradayDialog = false },
            title = { Text("સંપ્રદાય પસંદ કરો") },
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
                            Text(text = samp.nameGu, style = MaterialTheme.typography.bodyLarge)
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
            title = { Text("અયનાંશ પદ્ધતિ પસંદ કરો") },
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
            title = { Text("થીમ પસંદ કરો") },
            text = {
                Column {
                    listOf("SYSTEM", "LIGHT", "DARK").forEach { mode ->
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
                            Text(text = mode, style = MaterialTheme.typography.bodyLarge)
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
            title = { Text("Privacy Policy / ગોપનીયતા નીતિ") },
            text = {
                Text(
                    "Jain Panchang is an ad-free, completely offline application dedicated to Jain religious practice. " +
                    "Zero personal data is gathered, transmitted, or sold. All astronomical and tithi calculations run locally on your device. " +
                    "Location permission (if granted) is strictly used on-device to compute local sunrise and sunset timings for your coordinates and is never shared over any network."
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) { Text("બરાબર છે (OK)") }
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
