package com.jainpanchang.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jainpanchang.engine.model.ChoghadiyaType
import com.jainpanchang.ui.theme.*
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onOpenCityPicker: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val panchang = uiState.panchang

    if (panchang == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = SaffronPrimary)
        }
        return
    }

    val currentDate = LocalDate.parse(panchang.dateIso)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. City and Date Header with quick navigation
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
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onOpenCityPicker() }
                                .padding(vertical = 4.dp, horizontal = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Select City",
                                tint = SaffronPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = panchang.location.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = SaffronPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = SaffronPrimary
                            )
                        }
                        Text(
                            text = "${currentDate.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"))} • ${panchang.vara.nameGu}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "VS ${panchang.vikramSamvat} • VNS ${panchang.veerNirvanSamvat}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Medium,
                            color = GoldenSecondary
                        )
                    }

                    Row {
                        IconButton(onClick = { viewModel.setDate(currentDate.minusDays(1)) }) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = "Previous Day")
                        }
                        IconButton(onClick = { viewModel.setDate(LocalDate.now()) }) {
                            Icon(Icons.Default.Today, contentDescription = "Today")
                        }
                        IconButton(onClick = { viewModel.setDate(currentDate.plusDays(1)) }) {
                            Icon(Icons.Default.ChevronRight, contentDescription = "Next Day")
                        }
                    }
                }
            }
        }

        // 2. Main Spiritual Banner: Jain Month, Paksha & Tithi
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SaffronPrimary),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${panchang.jainMonth.nameGu} માસ • ${panchang.jainMonth.nameEn} Month",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = panchang.tithi.fullDisplayNameGu,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${panchang.tithi.fullDisplayNameHi} (${panchang.tithi.fullDisplayNameEn})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    val tithiStart = ZonedDateTime.parse(panchang.tithi.startIso)
                    val tithiEnd = ZonedDateTime.parse(panchang.tithi.endIso)
                    Text(
                        text = "તિથિ સમાપ્તિ: ${tithiEnd.format(DateTimeFormatter.ofPattern("h:mm a"))} સુધી",
                        style = MaterialTheme.typography.labelLarge,
                        color = GoldenSecondaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // 3. Live Highlighted Choghadiya Card
        item {
            val choghadiya = uiState.currentChoghadiya
            if (choghadiya != null) {
                val (cardColor, textColor) = when (choghadiya.type) {
                    ChoghadiyaType.AMRIT -> Pair(ColorAmrit, Color.White)
                    ChoghadiyaType.SHUBH -> Pair(ColorShubh, Color.White)
                    ChoghadiyaType.LABH -> Pair(ColorLabh, Color.White)
                    ChoghadiyaType.CHAR -> Pair(ColorChar, Color.White)
                    ChoghadiyaType.ROG -> Pair(ColorRog, Color.White)
                    ChoghadiyaType.KAAL -> Pair(ColorKaal, Color.White)
                    ChoghadiyaType.UDVEG -> Pair(ColorUdveg, Color.White)
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (choghadiya.isDay) "ચાલુ ચોઘડિયું (દિવસ)" else "ચાલુ ચોઘડિયું (રાત્રિ)",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = textColor.copy(alpha = 0.9f)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${choghadiya.type.nameGu} (${choghadiya.type.nameEn})",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            val s = ZonedDateTime.parse(choghadiya.startIso).format(DateTimeFormatter.ofPattern("h:mm a"))
                            val e = ZonedDateTime.parse(choghadiya.endIso).format(DateTimeFormatter.ofPattern("h:mm a"))
                            Text(
                                text = "$s થી $e",
                                style = MaterialTheme.typography.bodyMedium,
                                color = textColor.copy(alpha = 0.85f)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${uiState.choghadiyaCountdownMinutes} મિનિટ",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            Text(
                                text = "બાકી સમય",
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }

        // 4. Next Key Timing Strip
        item {
            val next = uiState.nextTiming
            if (next != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = GoldenSecondaryContainer.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = GoldenSecondary
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "આગામી સમય: ${next.first}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = next.second,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )
                    }
                }
            }
        }

        // 5. Sun and Moon Timings
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "સૂર્ય અને ચંદ્ર સમય (Sun & Moon)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = SaffronPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        val sunrise = ZonedDateTime.parse(panchang.solarTimes.sunriseIso).format(DateTimeFormatter.ofPattern("h:mm a"))
                        val sunset = ZonedDateTime.parse(panchang.solarTimes.sunsetIso).format(DateTimeFormatter.ofPattern("h:mm a"))
                        TimingCell("સૂર્યોદય (Sunrise)", sunrise, Icons.Default.WbSunny)
                        TimingCell("સૂર્યાસ્ત (Sunset)", sunset, Icons.Default.NightsStay)
                        val mr = panchang.lunarTimes.moonriseIso?.let { ZonedDateTime.parse(it).format(DateTimeFormatter.ofPattern("h:mm a")) } ?: "--:--"
                        TimingCell("ચંદ્રોદય (Moonrise)", mr, Icons.Default.Brightness2)
                    }
                }
            }
        }

        // 6. Panchang 4 Pillars Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "પંચાંગ અંગ (Panchang Details)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = SaffronPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PanchangElementItem("નક્ષત્ર", "${panchang.nakshatra.nameGu} (${panchang.nakshatra.nameEn})")
                        PanchangElementItem("યોગ", "${panchang.yoga.nameGu} (${panchang.yoga.nameEn})")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PanchangElementItem("કરણ", "${panchang.karana.nameGu} (${panchang.karana.nameEn})")
                        PanchangElementItem("વાર", "${panchang.vara.nameGu} (${panchang.vara.nameEn})")
                    }
                }
            }
        }

        // 7. Today's Kalyanaks & Festivals (if present)
        if (panchang.festivalsToday.isNotEmpty() || panchang.kalyanaksToday.isNotEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaroonTertiaryContainer.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Celebration, contentDescription = null, tint = MaroonTertiary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "આજના પર્વ અને કલ્યાણક",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaroonTertiary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        for (f in panchang.festivalsToday) {
                            Text(
                                text = "★ ${f.nameGu} (${f.nameEn})",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        for (k in panchang.kalyanaksToday) {
                            Text(
                                text = "✦ ${k.tirthankarNameGu} - ${k.type.nameGu}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TimingCell(label: String, time: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = time, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun PanchangElementItem(label: String, value: String) {
    Column(modifier = Modifier.width(150.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = SaffronPrimary, fontWeight = FontWeight.SemiBold)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}
