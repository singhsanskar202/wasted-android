package com.wasted.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wasted.data.model.DailyUsage
import com.wasted.ui.theme.WastedOrange

@Composable
fun HeatmapView(today: DailyUsage) {
    val peakSeconds = today.hourly.maxOrNull()?.coerceAtLeast(1) ?: 1
    val peakHour = if (peakSeconds > 0) today.hourly.indexOf(peakSeconds) else null

    Column(Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("MOST DISTRACTED HOURS", fontSize = 9.sp, letterSpacing = 1.sp, color = Color.Gray)
            Spacer(Modifier.weight(1f))
            if (peakHour != null && peakSeconds > 0) {
                Text("Peak: ${hourLabel(peakHour)}", fontSize = 9.sp, color = WastedOrange)
            }
        }
        Spacer(Modifier.height(12.dp))
        Canvas(Modifier.fillMaxWidth().height(80.dp)) {
            val gap = 2.dp.toPx()
            val barW = (size.width - 23 * gap) / 24
            today.hourly.forEachIndexed { hour, seconds ->
                val barH = if (seconds > 0)
                    (seconds.toFloat() / peakSeconds * size.height * 0.85f + 4.dp.toPx())
                else
                    4.dp.toPx()
                val x = hour * (barW + gap)
                val y = size.height - barH
                drawRoundRect(
                    color = if (seconds == peakSeconds && seconds > 0) WastedOrange else Color.White.copy(alpha = 0.2f),
                    topLeft = Offset(x, y),
                    size = Size(barW, barH),
                    cornerRadius = CornerRadius(2.dp.toPx())
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth()) {
            listOf("12a", "6a", "12p", "6p", "11p").forEachIndexed { i, label ->
                if (i > 0) Spacer(Modifier.weight(1f))
                Text(label, fontSize = 9.sp, color = Color(0xFF333333))
            }
        }
    }
}

private fun hourLabel(hour: Int): String {
    val h = if (hour % 12 == 0) 12 else hour % 12
    return "$h${if (hour < 12) "a" else "p"}"
}
