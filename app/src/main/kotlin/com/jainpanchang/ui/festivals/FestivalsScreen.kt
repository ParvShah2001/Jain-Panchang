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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jainpanchang.R
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
    val lang = LocalAppLanguage.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }
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
            text = stringResource(R.string.title_festivals),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = SaffronPrimary,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
        )

        Text(
            text = "${stringResource(R.string.select_sampraday)}: ${userSettings.sampraday.displayName(lang)}",
            style = MaterialTheme.typography.labelLarge,
            color = GoldenSecondary,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(stringResource(R.string.search_festival_hint)) },
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
                text = { Text("${stringResource(R.string.tab_all_festivals)} (${filteredFestivals.size})") }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("${stringResource(R.string.tab_kalyanaks)} (${filteredKalyanaks.size})") }
            )
            Tab(
                selected = selectedTabIndex == 2,
                onClick = { selectedTabIndex = 2 },
                text = { Text("${stringResource(R.string.tab_scholar_review)} (${reviewItemsFestivals.size + reviewItemsKalyanaks.size})") }
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
                        FestivalCard(fest, lang)
                    }
                }
            }
            1 -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredKalyanaks) { kal ->
                        KalyanakCard(kal, lang)
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
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = GoldenSecondary)
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = stringResource(R.string.needs_scholar_review_badge),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    items(reviewItemsFestivals) { f ->
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "★ ${f.displayName(lang)}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaroonTertiary
                                )
                                Text(
                                    text = "Rule: ${f.jainMonth.displayName(lang)} ${f.paksha.displayName(lang)} Tithi ${f.tithi}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                if (f.description.isNotEmpty()) {
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
}

@Composable
private fun FestivalCard(fest: Festival, lang: AppLanguage) {
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
                    text = fest.displayName(lang),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SaffronPrimary
                )
                Text(
                    text = "${fest.paksha.displayName(lang)} ${fest.tithi}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = GoldenSecondary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${stringResource(R.string.tithi)}: ${fest.jainMonth.displayName(lang)} (${fest.paksha.displayName(lang)} ${fest.tithi})",
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
private fun KalyanakCard(kal: Kalyanak, lang: AppLanguage) {
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
                    text = "${kal.tirthankarId}. ${kal.tirthankarDisplayName(lang)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${kal.type.displayName(lang)}${if (kal.notes.isNotEmpty()) " • ${kal.notes}" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${kal.jainMonth.displayName(lang)} ${kal.paksha.displayName(lang)} ${kal.tithi}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = SaffronPrimary
            )
        }
    }
}
