package com.jainpanchang.ui.events

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jainpanchang.R
import com.jainpanchang.data.db.MyEventEntity
import com.jainpanchang.data.repository.EventsRepository
import com.jainpanchang.data.repository.SettingsRepository
import com.jainpanchang.engine.model.JainMonth
import com.jainpanchang.engine.model.Paksha
import com.jainpanchang.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEventsScreen(
    eventsRepository: EventsRepository,
    settingsRepository: SettingsRepository,
    modifier: Modifier = Modifier
) {
    val events by eventsRepository.getAllEvents().collectAsState(initial = emptyList())
    val settings by settingsRepository.settingsFlow.collectAsState(initial = null)
    val userSettings = settings ?: return
    val scope = rememberCoroutineScope()
    val lang = LocalAppLanguage.current

    var showAddDialog by remember { mutableStateOf(false) }
    val thisYear = remember { LocalDate.now().year }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = SaffronPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_event))
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = stringResource(R.string.title_events),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = SaffronPrimary,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = stringResource(R.string.recurring_by_tithi_desc),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (events.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(48.dp), tint = GoldenSecondary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = stringResource(R.string.no_events_yet),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                items(events) { event ->
                    val computedDate = remember(event, thisYear, userSettings.selectedCity) {
                        eventsRepository.computeEventDateForYear(event, thisYear, userSettings.selectedCity)
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = event.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SaffronPrimary
                                )

                                val recurrenceLabel = if (event.isTithiBased) {
                                    val m = try { JainMonth.valueOf(event.jainMonth).displayName(lang) } catch (e: Exception) { event.jainMonth }
                                    val p = try { Paksha.valueOf(event.paksha).displayName(lang) } catch (e: Exception) { event.paksha }
                                    "$m $p ${stringResource(R.string.tithi)} ${event.tithi}"
                                } else {
                                    "${event.gregorianDay}/${event.gregorianMonth}"
                                }

                                Text(
                                    text = "${stringResource(R.string.recurrence)}: $recurrenceLabel",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$thisYear: ${computedDate.format(DateTimeFormatter.ofPattern("d MMMM yyyy (EEEE)", lang.locale))}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GoldenSecondary
                                )
                            }

                            IconButton(onClick = { scope.launch { eventsRepository.deleteEvent(event) } }) {
                                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete), tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Event Dialog
    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var isTithiBased by remember { mutableStateOf(true) }
        var selectedMonth by remember { mutableStateOf(JainMonth.KARTIKA) }
        var selectedPaksha by remember { mutableStateOf(Paksha.SHUKLA) }
        var selectedTithi by remember { mutableIntStateOf(1) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text(stringResource(R.string.add_event)) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(stringResource(R.string.event_title)) },
                        placeholder = { Text(stringResource(R.string.event_title_hint)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = isTithiBased,
                            onClick = { isTithiBased = true }
                        )
                        Text(text = stringResource(R.string.tithi_based))
                    }

                    if (isTithiBased) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "${stringResource(R.string.tithi)}: ${selectedMonth.displayName(lang)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            TextButton(onClick = {
                                val nextIdx = (selectedMonth.ordinal + 1) % 12
                                selectedMonth = JainMonth.entries[nextIdx]
                            }) { Text(selectedMonth.displayName(lang)) }

                            TextButton(onClick = {
                                selectedPaksha = if (selectedPaksha == Paksha.SHUKLA) Paksha.KRISHNA else Paksha.SHUKLA
                            }) { Text(selectedPaksha.displayName(lang)) }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${stringResource(R.string.tithi)} (1-15): ")
                            Slider(
                                value = selectedTithi.toFloat(),
                                onValueChange = { selectedTithi = it.toInt() },
                                valueRange = 1f..15f,
                                steps = 13,
                                modifier = Modifier.weight(1f)
                            )
                            Text("$selectedTithi", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            scope.launch {
                                eventsRepository.addEvent(
                                    MyEventEntity(
                                        title = title.trim(),
                                        isTithiBased = isTithiBased,
                                        jainMonth = selectedMonth.name,
                                        paksha = selectedPaksha.name,
                                        tithi = selectedTithi
                                    )
                                )
                                showAddDialog = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text(stringResource(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}
