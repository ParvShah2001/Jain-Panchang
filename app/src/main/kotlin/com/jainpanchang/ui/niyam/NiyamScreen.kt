package com.jainpanchang.ui.niyam

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jainpanchang.data.repository.NiyamRepository
import com.jainpanchang.data.schema.ChaudahNiyamEntry
import com.jainpanchang.data.schema.NiyamSuggestionEntry
import com.jainpanchang.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NiyamScreen(
    niyamRepository: NiyamRepository,
    modifier: Modifier = Modifier
) {
    val today = remember { LocalDate.now() }
    val todayIso = remember { today.toString() }
    val scope = rememberCoroutineScope()

    val categories = remember { niyamRepository.getCategories() }
    val chaudahNiyams = remember { niyamRepository.getChaudahNiyams() }
    val dailySuggestion = remember { niyamRepository.getDailySuggestion(today) }

    val todayRecords by niyamRepository.getRecordsForDate(todayIso).collectAsState(initial = emptyList())
    val completedSet = remember(todayRecords) {
        todayRecords.filter { it.completed }.map { it.niyamId }.toSet()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "દૈનિક નિયમ (Daily Niyam)",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = SaffronPrimary,
                modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
            )
        }

        // 1. Streak and Today's Progress Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = GoldenSecondaryContainer),
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
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GoldenSecondary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "આજની પ્રગતિ (Today's Progress)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldenOnSecondaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${completedSet.size} / 14 નિયમ પૂર્ણ થયા",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldenOnSecondaryContainer
                        )
                    }

                    // Progress circular badge
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(SaffronPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        val pct = ((completedSet.size / 14.0) * 100).toInt()
                        Text(
                            text = "$pct%",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // 2. Today's Special Suggestion Card
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = SaffronPrimaryContainer.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = SaffronPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "આજનું પ્રેરણાદાયી સૂચન (Daily Suggestion)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = dailySuggestion.title.gu,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = dailySuggestion.title.en,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = dailySuggestion.detail.gu,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // 3. Classical Chaudah Niyam List
        item {
            Text(
                text = "ચૌદહ નિયમ (14 Classical Niyams)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = SaffronPrimary
            )
        }

        items(chaudahNiyams) { niyam ->
            val isChecked = completedSet.contains(niyam.id)
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (isChecked) ColorAmrit.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            scope.launch {
                                niyamRepository.toggleNiyamCompletion(todayIso, niyam.id, !isChecked)
                            }
                        }
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = niyam.name.gu,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isChecked) ColorAmrit else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = niyam.name.en,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = niyam.description.gu,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "મર્યાદા: ${niyam.defaultMaryada}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldenSecondary
                        )
                    }

                    Checkbox(
                        checked = isChecked,
                        onCheckedChange = { checked ->
                            scope.launch {
                                niyamRepository.toggleNiyamCompletion(todayIso, niyam.id, checked)
                            }
                        }
                    )
                }
            }
        }
    }
}
