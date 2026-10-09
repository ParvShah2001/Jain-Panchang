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
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.first

class TodayPanchangWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val container = DefaultAppContainer(context)
        val settings = container.settingsRepository.settingsFlow.first()
        val today = LocalDate.now()
        val panchang = container.panchangRepository.getDailyPanchang(today, settings.selectedCity, settings.ayanamsha)

        val sunrise = ZonedDateTime.parse(panchang.solarTimes.sunriseIso).format(DateTimeFormatter.ofPattern("h:mm a"))
        val sunset = ZonedDateTime.parse(panchang.solarTimes.sunsetIso).format(DateTimeFormatter.ofPattern("h:mm a"))

        provideContent {
            GlanceTheme {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(Color(0xFFFCF9F6))
                        .cornerRadius(16.dp)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "${panchang.location.name} • ${panchang.vara.nameGu}",
                            style = TextStyle(fontSize = 11.sp, color = androidx.glance.unit.ColorProvider(Color(0xFF8B6508)))
                        )
                    }

                    Spacer(modifier = GlanceModifier.height(4.dp))

                    Text(
                        text = panchang.tithi.fullDisplayNameGu,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = androidx.glance.unit.ColorProvider(Color(0xFFC85A17))
                        )
                    )

                    Text(
                        text = "${panchang.jainMonth.nameGu} (${panchang.jainMonth.nameEn})",
                        style = TextStyle(fontSize = 12.sp, color = androidx.glance.unit.ColorProvider(Color(0xFF53433E)))
                    )

                    Spacer(modifier = GlanceModifier.height(6.dp))

                    Row(
                        modifier = GlanceModifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "સૂર્યોદય: $sunrise  •  સૂર્યાસ્ત: $sunset",
                            style = TextStyle(
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = androidx.glance.unit.ColorProvider(Color(0xFF201A18))
                            )
                        )
                    }
                }
            }
        }
    }
}

class TodayPanchangWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TodayPanchangWidget()
}
