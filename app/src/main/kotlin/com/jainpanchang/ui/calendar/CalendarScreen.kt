package com.jainpanchang.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jainpanchang.engine.model.DailyPanchang
import com.jainpanchang.engine.model.Paksha
import com.jainpanchang.ui.theme.*
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val ym = uiState.selectedYearMonth
    val daysList = uiState.monthPanchangList
    val selectedDay = uiState.selectedDayPanchang
    var showYearDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Month Header and Switcher
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.prevMonth() }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Month")
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showYearDialog = true }
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "${ym.month.name} ${ym.year}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Select Year", tint = SaffronPrimary)
                    }

                    IconButton(onClick = { viewModel.nextMonth() }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Next Month")
                    }
                }
            }
        }

        // 2. Day-of-Week headers
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                val dow = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                for (d in dow) {
                    Text(
                        text = d,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (d == "Sun") Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.width(42.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // 3. Month Grid
        item {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = SaffronPrimary)
                }
            } else {
                val firstDayOfMonth = ym.atDay(1)
                val emptySlotsBefore = firstDayOfMonth.dayOfWeek.value % 7 // Sun=0, Mon=1...

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val allCells = (0 until emptySlotsBefore).map { null } + daysList
                    val rows = allCells.chunked(7)

                    for (row in rows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            for (cell in row) {
                                if (cell == null) {
                                    Spacer(modifier = Modifier.width(44.dp))
                                } else {
                                    CalendarDayCell(
                                        panchang = cell,
                                        isSelected = selectedDay?.dateIso == cell.dateIso,
                                        isToday = cell.dateIso == LocalDate.now().toString(),
                                        onClick = { viewModel.selectDay(cell) }
                                    )
                                }
                            }
                            if (row.size < 7) {
                                for (i in 0 until (7 - row.size)) {
                                    Spacer(modifier = Modifier.width(44.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Selected Day Detail Card
        if (selectedDay != null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        val d = LocalDate.parse(selectedDay.dateIso)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = d.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SaffronPrimary
                                )
                                Text(
                                    text = "${selectedDay.jainMonth.nameGu} (${selectedDay.jainMonth.nameEn}) • ${selectedDay.vara.nameGu}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "VS ${selectedDay.vikramSamvat}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldenSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        // Tithi with end time
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "તિથિ: ${selectedDay.tithi.fullDisplayNameGu}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            val end = ZonedDateTime.parse(selectedDay.tithi.endIso).format(DateTimeFormatter.ofPattern("h:mm a"))
                            Text(
                                text = "સમાપ્તિ: $end",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SaffronPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Sun times
                        val sr = ZonedDateTime.parse(selectedDay.solarTimes.sunriseIso).format(DateTimeFormatter.ofPattern("h:mm a"))
                        val ss = ZonedDateTime.parse(selectedDay.solarTimes.sunsetIso).format(DateTimeFormatter.ofPattern("h:mm a"))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "સૂર્યોદય: $sr  •  સૂર્યાસ્ત (ચૌવિહાર): $ss",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // Nakshatra, Yoga
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "નક્ષત્ર: ${selectedDay.nakshatra.nameGu}  •  યોગ: ${selectedDay.yoga.nameGu}  •  કરણ: ${selectedDay.karana.nameGu}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Festivals / Kalyanaks
                        if (selectedDay.festivalsToday.isNotEmpty() || selectedDay.kalyanaksToday.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            for (f in selectedDay.festivalsToday) {
                                Text(
                                    text = "★ ${f.nameGu} (${f.nameEn})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaroonTertiary
                                )
                            }
                            for (k in selectedDay.kalyanaksToday) {
                                Text(
                                    text = "✦ ${k.tirthankarNameGu} - ${k.type.nameGu}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldenSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Quick Year Selector Dialog (1970 to 2070 - 100 years browse)
    if (showYearDialog) {
        AlertDialog(
            onDismissRequest = { showYearDialog = false },
            title = { Text("વર્ષ પસંદ કરો (Select Year)") },
            text = {
                val years = (1970..2070).toList()
                LazyColumn(modifier = Modifier.height(280.dp)) {
                    items(years.size) { idx ->
                        val y = years[idx]
                        Text(
                            text = "$y",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (y == ym.year) FontWeight.Bold else FontWeight.Normal,
                            color = if (y == ym.year) SaffronPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.jumpToYearMonth(y, ym.monthValue)
                                    showYearDialog = false
                                }
                                .padding(vertical = 10.dp, horizontal = 16.dp)
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showYearDialog = false }) { Text("બંધ કરો (Close)") }
            }
        )
    }
}

@Composable
private fun CalendarDayCell(
    panchang: DailyPanchang,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit
) {
    val date = LocalDate.parse(panchang.dateIso)
    val hasEvent = panchang.festivalsToday.isNotEmpty() || panchang.kalyanaksToday.isNotEmpty()
    val isPurnimaOrAmas = panchang.tithi.number == 15

    val bgColor = when {
        isSelected -> SaffronPrimary
        isToday -> SaffronPrimaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
    }

    val textColor = when {
        isSelected -> Color.White
        isToday -> SaffronOnPrimaryContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 52.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "${date.dayOfMonth}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = textColor
            )

            // Short tithi text (e.g. સુદ ૫, વદ ૧૧, પૂનમ)
            val shortPaksha = if (panchang.tithi.paksha == Paksha.SHUKLA) "સુ" else "વ"
            val shortTithi = when (panchang.tithi.number) {
                15 -> if (panchang.tithi.paksha == Paksha.SHUKLA) "પૂ" else "અ"
                else -> "$shortPaksha ${panchang.tithi.number}"
            }
            Text(
                text = shortTithi,
                fontSize = 9.sp,
                lineHeight = 10.sp,
                fontWeight = if (isPurnimaOrAmas) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White.copy(alpha = 0.9f) else GoldenSecondary
            )

            // Dot for Festival / Kalyanak
            if (hasEvent) {
                Box(
                    modifier = Modifier
                        .padding(top = 1.dp)
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else MaroonTertiary)
                )
            }
        }
    }
}
