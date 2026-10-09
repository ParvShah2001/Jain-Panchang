package com.jainpanchang.ui.timings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jainpanchang.engine.model.*
import com.jainpanchang.ui.home.HomeViewModel
import com.jainpanchang.ui.theme.*
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimingsScreen(
    homeViewModel: HomeViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by homeViewModel.uiState.collectAsState()
    val panchang = uiState.panchang

    if (panchang == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = SaffronPrimary)
        }
        return
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("ચોઘડિયા", "પચ્ચખાણ", "હોરા", "ગૌરી", "મુહૂર્ત")

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "સમય અને મુહૂર્ત (Timings)",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = SaffronPrimary,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        PrimaryScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 0.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = SaffronPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedTabIndex) {
            0 -> ChoghadiyaTabContent(panchang, uiState.currentChoghadiya)
            1 -> PachkhanTabContent(panchang)
            2 -> HoraTabContent(panchang)
            3 -> GowriTabContent(panchang)
            4 -> MuhuratTabContent(panchang)
        }
    }
}

@Composable
private fun ChoghadiyaTabContent(panchang: DailyPanchang, currentSlot: ChoghadiyaSlot?) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "દિવસના ચોઘડિયા (Day Choghadiya)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SaffronPrimary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        items(panchang.dayChoghadiya) { slot ->
            ChoghadiyaCardItem(slot, isCurrent = currentSlot?.startIso == slot.startIso && currentSlot.isDay)
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "રાત્રિના ચોઘડિયા (Night Choghadiya)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GoldenSecondary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        items(panchang.nightChoghadiya) { slot ->
            ChoghadiyaCardItem(slot, isCurrent = currentSlot?.startIso == slot.startIso && !currentSlot.isDay)
        }
    }
}

@Composable
private fun ChoghadiyaCardItem(slot: ChoghadiyaSlot, isCurrent: Boolean) {
    val (typeColor, textColor) = when (slot.type) {
        ChoghadiyaType.AMRIT -> Pair(ColorAmrit, Color.White)
        ChoghadiyaType.SHUBH -> Pair(ColorShubh, Color.White)
        ChoghadiyaType.LABH -> Pair(ColorLabh, Color.White)
        ChoghadiyaType.CHAR -> Pair(ColorChar, Color.White)
        ChoghadiyaType.ROG -> Pair(ColorRog, Color.White)
        ChoghadiyaType.KAAL -> Pair(ColorKaal, Color.White)
        ChoghadiyaType.UDVEG -> Pair(ColorUdveg, Color.White)
    }

    val s = ZonedDateTime.parse(slot.startIso).format(DateTimeFormatter.ofPattern("h:mm a"))
    val e = ZonedDateTime.parse(slot.endIso).format(DateTimeFormatter.ofPattern("h:mm a"))

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) typeColor else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (isCurrent) Color.White else typeColor)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "${slot.type.nameGu} (${slot.type.nameEn})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isCurrent) textColor else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "સ્વામી: ${slot.type.rulerPlanet}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isCurrent) textColor.copy(alpha = 0.85f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$s - $e",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCurrent) textColor else MaterialTheme.colorScheme.onSurface
                )
                if (isCurrent) {
                    Text(
                        text = "● ચાલુ (ACTIVE)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
private fun PachkhanTabContent(panchang: DailyPanchang) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SaffronPrimaryContainer.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "પચ્ચખાણના સમય સ્થાનિક સૂર્યોદય અને દિવસના ચોથા ભાગ (પ્રહર) અનુસાર ગણવામાં આવે છે.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }

        items(panchang.pachkhans) { p ->
            val time = ZonedDateTime.parse(p.timeIso).format(DateTimeFormatter.ofPattern("h:mm a"))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                shape = RoundedCornerShape(14.dp),
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
                            text = "${p.nameGu} (${p.nameEn})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )
                        Text(
                            text = p.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = time,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun HoraTabContent(panchang: DailyPanchang) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(panchang.horas) { hora ->
            val s = ZonedDateTime.parse(hora.startIso).format(DateTimeFormatter.ofPattern("h:mm a"))
            val e = ZonedDateTime.parse(hora.endIso).format(DateTimeFormatter.ofPattern("h:mm a"))
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${hora.hourIndex}. ${hora.nameGu} હોરા (${hora.planetName})",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "$s - $e",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun GowriTabContent(panchang: DailyPanchang) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "દિવસનું ગૌરી પંચાંગ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SaffronPrimary
            )
        }
        items(panchang.gowriDay) { slot ->
            GowriSlotRow(slot)
        }
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "રાત્રિનું ગૌરી પંચાંગ",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GoldenSecondary
            )
        }
        items(panchang.gowriNight) { slot ->
            GowriSlotRow(slot)
        }
    }
}

@Composable
private fun GowriSlotRow(slot: GowriSlot) {
    val s = ZonedDateTime.parse(slot.startIso).format(DateTimeFormatter.ofPattern("h:mm a"))
    val e = ZonedDateTime.parse(slot.endIso).format(DateTimeFormatter.ofPattern("h:mm a"))
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (slot.isAuspicious) ColorAmrit.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (slot.isAuspicious) "✓" else "✕",
                    color = if (slot.isAuspicious) ColorAmrit else ColorKaal,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${slot.nameGu} (${slot.nameEn})",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = "$s - $e",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MuhuratTabContent(panchang: DailyPanchang) {
    LazyColumn(
        contentPadding = PaddingValues(bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(panchang.muhurats) { m ->
            val s = ZonedDateTime.parse(m.startIso).format(DateTimeFormatter.ofPattern("h:mm a"))
            val e = ZonedDateTime.parse(m.endIso).format(DateTimeFormatter.ofPattern("h:mm a"))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (m.isAuspicious) ColorAmrit.copy(alpha = 0.15f) else ColorKaal.copy(alpha = 0.12f)
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
                            text = "${m.nameGu} (${m.nameEn})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (m.isAuspicious) ColorAmrit else ColorKaal
                        )
                        Text(
                            text = "$s - $e",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (m.description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = m.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
