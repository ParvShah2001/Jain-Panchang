package com.jainpanchang.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jainpanchang.R
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
    val lang = LocalAppLanguage.current
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
                            text = ym.format(DateTimeFormatter.ofPattern("MMMM yyyy", lang.locale)),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = stringResource(R.string.select_year), tint = SaffronPrimary)
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
                val dow = when (lang) {
                    AppLanguage.GUJARATI -> listOf("રવિ", "સોમ", "મંગળ", "બુધ", "ગુરુ", "શુક્ર", "શનિ")
                    AppLanguage.HINDI -> listOf("रवि", "सोम", "मंगल", "बुध", "गुरु", "शुक्र", "शनि")
                    AppLanguage.ENGLISH -> listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                }
                for ((idx, d) in dow.withIndex()) {
                    Text(
                        text = d,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (idx == 0) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant,
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
                                    Spacer(modifier = Modifier.size(width = 44.dp, height = 52.dp))
                                } else {
                                    val cellDate = LocalDate.parse(cell.dateIso)
                                    val isToday = cellDate == LocalDate.now()
                                    val isSelected = selectedDay?.dateIso == cell.dateIso
                                    CalendarDayCell(
                                        panchang = cell,
                                        isSelected = isSelected,
                                        isToday = isToday,
                                        lang = lang,
                                        onClick = { viewModel.selectDay(cell) }
                                    )
                                }
                            }
                            if (row.size < 7) {
                                for (i in 0 until (7 - row.size)) {
                                    Spacer(modifier = Modifier.size(width = 44.dp, height = 52.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Day Details Card
        item {
            if (selectedDay != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
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
                                    text = d.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", lang.locale)),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SaffronPrimary
                                )
                                Text(
                                    text = "${selectedDay.jainMonth.displayName(lang)} • ${selectedDay.vara.displayName(lang)}",
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
                                text = "${stringResource(R.string.tithi)}: ${selectedDay.tithi.displayName(lang)}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                            val end = ZonedDateTime.parse(selectedDay.tithi.endIso).format(DateTimeFormatter.ofPattern("h:mm a"))
                            Text(
                                text = "${stringResource(R.string.ends_at)} $end",
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
                            text = "${stringResource(R.string.sunrise)}: $sr  •  ${stringResource(R.string.sunset)}: $ss",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        // Nakshatra, Yoga, Karana
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${stringResource(R.string.nakshatra)}: ${selectedDay.nakshatra.displayName(lang)}  •  ${stringResource(R.string.yoga)}: ${selectedDay.yoga.displayName(lang)}  •  ${stringResource(R.string.karana)}: ${selectedDay.karana.displayName(lang)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Festivals / Kalyanaks
                        if (selectedDay.festivalsToday.isNotEmpty() || selectedDay.kalyanaksToday.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            for (f in selectedDay.festivalsToday) {
                                Text(
                                    text = "★ ${f.displayName(lang)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaroonTertiary
                                )
                            }
                            for (k in selectedDay.kalyanaksToday) {
                                Text(
                                    text = "✦ ${k.displayName(lang)}",
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
            title = { Text(stringResource(R.string.select_year)) },
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
                TextButton(onClick = { showYearDialog = false }) { Text(stringResource(R.string.close)) }
            }
        )
    }
}

@Composable
private fun CalendarDayCell(
    panchang: DailyPanchang,
    isSelected: Boolean,
    isToday: Boolean,
    lang: AppLanguage,
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

            // Short tithi text
            val shortPaksha = when (lang) {
                AppLanguage.GUJARATI -> if (panchang.tithi.paksha == Paksha.SHUKLA) "સુ" else "વ"
                AppLanguage.HINDI -> if (panchang.tithi.paksha == Paksha.SHUKLA) "शु" else "कृ"
                AppLanguage.ENGLISH -> if (panchang.tithi.paksha == Paksha.SHUKLA) "S" else "K"
            }
            val shortTithi = when (panchang.tithi.number) {
                15 -> when (lang) {
                    AppLanguage.GUJARATI -> if (panchang.tithi.paksha == Paksha.SHUKLA) "પૂ" else "અ"
                    AppLanguage.HINDI -> if (panchang.tithi.paksha == Paksha.SHUKLA) "पूर्णि" else "अमा"
                    AppLanguage.ENGLISH -> if (panchang.tithi.paksha == Paksha.SHUKLA) "Pur" else "Amv"
                }
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
