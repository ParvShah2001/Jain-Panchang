package com.jainpanchang.ui.festivals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jainpanchang.data.repository.PanchangRepository
import com.jainpanchang.data.repository.SettingsRepository
import com.jainpanchang.engine.model.Festival
import com.jainpanchang.engine.model.Kalyanak
import com.jainpanchang.engine.model.Sampraday
import com.jainpanchang.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FestivalsScreen(
    panchangRepository: PanchangRepository,
    settingsRepository: SettingsRepository,
    modifier: Modifier = Modifier
) {
    val settings by settingsRepository.settingsFlow.collectAsState(initial = null)
    val userSettings = settings ?: return

    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0=Festivals, 1=Kalyanaks, 2=Scholar Review
    var searchQuery by remember { mutableStateOf("") }

    val allFestivals = remember { panchangRepository.getFestivals() }
    val allKalyanaks = remember { panchangRepository.getKalyanaks() }

    // Filter festivals by Sampraday
    val filteredFestivals = remember(userSettings.sampraday, searchQuery) {
        val sId = userSettings.sampraday.id
        allFestivals.filter { f ->
            val matchesSampraday = f.sampradays.isEmpty() || f.sampradays.contains(sId)
            val matchesQuery = searchQuery.isEmpty() ||
                    f.nameEn.contains(searchQuery, ignoreCase = true) ||
                    f.nameGu.contains(searchQuery, ignoreCase = true) ||
                    f.nameHi.contains(searchQuery, ignoreCase = true)
            matchesSampraday && matchesQuery
        }
    }

    val filteredKalyanaks = remember(searchQuery) {
        allKalyanaks.filter { k ->
            searchQuery.isEmpty() ||
                    k.tirthankarNameEn.contains(searchQuery, ignoreCase = true) ||
                    k.tirthankarNameGu.contains(searchQuery, ignoreCase = true) ||
                    k.tirthankarNameHi.contains(searchQuery, ignoreCase = true) ||
                    k.type.nameEn.contains(searchQuery, ignoreCase = true)
        }
    }

    val reviewItemsFestivals = remember { allFestivals.filter { it.needsReview } }
    val reviewItemsKalyanaks = remember { allKalyanaks.filter { it.needsReview } }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "પર્વ અને કલ્યાણક (Festivals)",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = SaffronPrimary,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
        )

        Text(
            text = "સંપ્રદાય: ${userSettings.sampraday.nameGu}",
            style = MaterialTheme.typography.labelLarge,
            color = GoldenSecondary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("પર્વ અથવા તીર્થંકર શોધો...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        PrimaryTabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = SaffronPrimary
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("જૈન પર્વ (${filteredFestivals.size})") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("કલ્યાણક (${filteredKalyanaks.size})") }
            )
            Tab(
                selected = selectedTabIndex == 2,
                onClick = { selectedTabIndex = 2 },
                text = { Text("વિદ્વાન ચકાસણી (${reviewItemsFestivals.size + reviewItemsKalyanaks.size})") }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTabIndex) {
            0 -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredFestivals) { fest ->
                        FestivalCard(fest)
                    }
                }
            }
            1 -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredKalyanaks) { kal ->
                        KalyanakCard(kal)
                    }
                }
            }
            2 -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = GoldenSecondaryContainer),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "જૈન વિદ્વાન સમીક્ષા સૂચિ (Scholar Review Items)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldenOnSecondaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "આ સૂચિમાં રહેલા નિયમો અને તિથિઓ વિવિધ સંપ્રદાયો કે પરંપરાઓમાં મતભેદ ધરાવે છે અને scholar verification માટે 'needs_review: true' ચિહ્નિત છે.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GoldenOnSecondaryContainer
                                )
                            }
                        }
                    }

                    items(reviewItemsFestivals) { f ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "★ ${f.nameGu} (${f.nameEn})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaroonTertiary
                                )
                                Text(
                                    text = "નિયમ: ${f.jainMonth.nameGu} ${f.paksha.nameGu} તિથિ ${f.tithi}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = f.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FestivalCard(fest: Festival) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = fest.nameGu,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SaffronPrimary
                )
                Text(
                    text = "${fest.paksha.nameGu} ${fest.tithi}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = GoldenSecondary
                )
            }
            Text(
                text = "${fest.nameHi} • ${fest.nameEn}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "માસ: ${fest.jainMonth.nameGu} (${fest.jainMonth.nameEn})",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            if (fest.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = fest.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun KalyanakCard(kal: Kalyanak) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${kal.tirthankarId}. ${kal.tirthankarNameGu} (${kal.tirthankarNameEn})",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${kal.type.nameGu} • ${kal.notes}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${kal.jainMonth.nameGu} ${kal.paksha.nameGu} ${kal.tithi}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = SaffronPrimary
            )
        }
    }
}
