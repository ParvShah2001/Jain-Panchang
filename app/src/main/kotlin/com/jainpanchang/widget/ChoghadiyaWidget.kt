package com.jainpanchang.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.jainpanchang.di.DefaultAppContainer
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ChoghadiyaWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val container = DefaultAppContainer(context)
        val settings = container.settingsRepository.settingsFlow.first()
        val today = LocalDate.now()
        val panchang = container.panchangRepository.getDailyPanchang(today, settings.selectedCity, settings.ayanamsha)

        val now = ZonedDateTime.now(panchang.location.zoneId)
        val allSlots = panchang.dayChoghadiya + panchang.nightChoghadiya
        val current = allSlots.firstOrNull { slot ->
            val start = ZonedDateTime.parse(slot.startIso)
            val end = ZonedDateTime.parse(slot.endIso)
            (now.isEqual(start) || now.isAfter(start)) && now.isBefore(end)
        } ?: allSlots.first()

        val s = ZonedDateTime.parse(current.startIso).format(DateTimeFormatter.ofPattern("h:mm a"))
        val e = ZonedDateTime.parse(current.endIso).format(DateTimeFormatter.ofPattern("h:mm a"))

        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(Color(0xFFC85A17))
                        .cornerRadius(16.dp)
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (current.isDay) "ચાલુ ચોઘડિયું (દિવસ)" else "ચાલુ ચોઘડિયું (રાત્રિ)",
                        style = TextStyle(fontSize = 11.sp, color = androidx.glance.unit.ColorProvider(Color.White.copy(alpha = 0.85f)))
                    )
                    Spacer(modifier = GlanceModifier.height(2.dp))
                    Text(
                        text = "${current.type.nameGu} (${current.type.nameEn})",
                        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold, color = androidx.glance.unit.ColorProvider(Color.White))
                    )
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = "$s થી $e",
                        style = TextStyle(fontSize = 12.sp, color = androidx.glance.unit.ColorProvider(Color.White.copy(alpha = 0.9f)))
                    )
                }
            }
        }
    }
}

class ChoghadiyaWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ChoghadiyaWidget()
}
