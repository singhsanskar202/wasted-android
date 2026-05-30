package com.wasted.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wasted.data.model.DangerZone
import com.wasted.data.model.InsightResult
import com.wasted.domain.InsightEngine
import com.wasted.ui.theme.WastedCardBg
import com.wasted.ui.theme.WastedOrange
import com.wasted.ui.theme.WastedRed

@Composable
fun DangerZonesCard(result: InsightResult) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(WastedCardBg)
    ) {
        // Header
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val title = when (result.tone) {
                InsightResult.Tone.POSITIVE -> "CLEAN ZONES"
                InsightResult.Tone.NEUTRAL  -> "USAGE PATTERN"
                InsightResult.Tone.WARNING  -> "DANGER ZONES"
            }
            Text(title, fontSize = 9.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 2.sp, color = Color(0xFF404040))
            Spacer(Modifier.weight(1f))
            Text("TODAY", fontSize = 9.sp, color = Color(0xFF2E2E2E))
        }
        HorizontalDivider(color = Color(0xFF141414))

        // Timeline strip (24 hour color bar)
        Column {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 14.dp)
                    .height(22.dp)
                    .clip(RoundedCornerShape(5.dp)),
                horizontalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                result.timelineSegments.forEach { level ->
                    Box(Modifier.weight(1f).fillMaxHeight().background(segmentColor(level)))
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 5.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("12am", "6am", "12pm", "6pm", "11pm").forEach { label ->
                    Text(label, fontSize = 8.sp, color = Color(0xFF333333))
                }
            }
        }

        // Legend
        Row(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            listOf(
                DangerZone.Level.LOW to "low",
                DangerZone.Level.MODERATE to "moderate",
                DangerZone.Level.DANGER to "danger"
            ).forEach { (level, label) ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(segmentColor(level))
                    )
                    Text(label, fontSize = 9.sp, color = Color(0xFF404040))
                }
            }
        }

        HorizontalDivider(color = Color(0xFF141414))

        // Zone list or empty state
        val nonClean = result.zones.filter { it.level != DangerZone.Level.CLEAN }
        if (nonClean.isEmpty()) {
            Text(
                "All clear today.",
                fontSize = 13.sp, color = Color(0xFF404040),
                modifier = Modifier.padding(16.dp)
            )
        } else {
            nonClean.forEach { zone ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(InsightEngine.timeRangeLabel(zone.startHour, zone.endHour), fontSize = 13.sp, color = Color.White)
                    Text(formatSeconds(zone.seconds), fontSize = 13.sp, color = levelColor(zone.level))
                }
            }
        }

        HorizontalDivider(color = Color(0xFF141414))

        // Verdict
        Text(
            result.verdictLine,
            fontSize = 12.sp, color = Color(0xFF505050),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
        )
    }
}

private fun segmentColor(level: DangerZone.Level) = when (level) {
    DangerZone.Level.CLEAN    -> Color(0xFF1A1A1A)
    DangerZone.Level.LOW      -> Color(0xFF2E1A0D)
    DangerZone.Level.MODERATE -> Color(0xFF7A300F)
    DangerZone.Level.DANGER   -> WastedRed
}

private fun levelColor(level: DangerZone.Level) = when (level) {
    DangerZone.Level.LOW      -> WastedOrange.copy(alpha = 0.5f)
    DangerZone.Level.MODERATE -> WastedOrange
    DangerZone.Level.DANGER   -> WastedRed
    else                      -> Color.White
}

private fun formatSeconds(s: Int): String {
    val h = s / 3600
    val m = (s % 3600) / 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}
