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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wasted.data.model.WeeklyInsight
import com.wasted.ui.theme.WastedCardBg
import com.wasted.ui.theme.WastedRed

@Composable
fun WeeklyCard(weekly: WeeklyInsight) {
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
            Text(
                "THIS WEEK",
                fontSize = 9.sp, fontWeight = FontWeight.SemiBold,
                letterSpacing = 2.sp, color = Color(0xFF404040)
            )
            Spacer(Modifier.weight(1f))
            val (label, color) = when (weekly.trend) {
                WeeklyInsight.Trend.IMPROVING -> "↓ IMPROVING" to Color(0xFF4CD964)
                WeeklyInsight.Trend.WORSENING -> "↑ WORSENING" to WastedRed
                WeeklyInsight.Trend.FLAT      -> "→ FLAT"       to Color(0xFF595959)
            }
            Text(label, fontSize = 8.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp, color = color)
        }
        HorizontalDivider(color = Color(0xFF141414))

        // Bar chart
        val maxVal = (weekly.totalSeconds.maxOrNull() ?: 1).coerceAtLeast(1)
        Column(Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(68.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                weekly.totalSeconds.forEachIndexed { idx, secs ->
                    val barH = ((secs.toFloat() / maxVal) * 64 + 2).dp
                    val isLast = idx == weekly.totalSeconds.lastIndex
                    Box(
                        Modifier
                            .weight(1f)
                            .height(barH)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                if (isLast) Color.White.copy(alpha = 0.9f)
                                else Color.White.copy(alpha = 0.15f)
                            )
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth()) {
                weekly.dateLabels.forEach { label ->
                    Text(
                        label,
                        fontSize = 8.sp, color = Color(0xFF383838),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        HorizontalDivider(color = Color(0xFF141414))
        Text(
            weekly.verdictLine,
            fontSize = 12.sp, color = Color(0xFF505050),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
        )
    }
}
