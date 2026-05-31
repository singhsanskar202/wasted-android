package com.wasted.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReceiptCard(
    totalSeconds: Int,
    appSeconds: Map<String, Int>,
    displayNames: Map<String, String>,
    modifier: Modifier = Modifier
) {
    val topApps = appSeconds.entries
        .sortedByDescending { it.value }
        .take(5)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .background(Color(0xFF0D0D0D), RoundedCornerShape(16.dp))
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "WASTED CO. • TODAY",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            letterSpacing = 2.sp,
            color = Color.White.copy(alpha = 0.3f)
        )
        Spacer(Modifier.height(8.dp))

        Text(
            text = "%,ds".format(totalSeconds),
            fontFamily = FontFamily.Monospace,
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(Modifier.height(2.dp))

        Text(
            text = "SECONDS YOU WON'T GET BACK",
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            letterSpacing = 1.5.sp,
            color = Color(0xFFFF4444)
        )
        Spacer(Modifier.height(20.dp))

        DashedDivider()
        Spacer(Modifier.height(16.dp))

        if (topApps.isEmpty()) {
            Text(
                text = "no tracked apps used today",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.3f)
            )
        } else {
            topApps.forEachIndexed { index, (pkg, seconds) ->
                val name = displayNames[pkg] ?: pkg.substringAfterLast('.')
                val fraction = if (totalSeconds > 0) seconds.toFloat() / totalSeconds else 0f
                val barColor = if (index == 0) Color(0xFFFF4444) else Color(0xFFFF6600)
                ReceiptRow(name = name, seconds = seconds, fraction = fraction, barColor = barColor)
                Spacer(Modifier.height(10.dp))
            }
        }

        Spacer(Modifier.height(6.dp))
        DashedDivider()
        Spacer(Modifier.height(16.dp))

        Text(
            text = "these seconds are non-refundable.",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = Color(0xFFFF4444).copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ReceiptRow(
    name: String,
    seconds: Int,
    fraction: Float,
    barColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.weight(1f),
            maxLines = 1
        )
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .width(72.dp)
                .height(3.dp)
                .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(2.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .background(barColor, RoundedCornerShape(2.dp))
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = "%,ds".format(seconds),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Composable
private fun DashedDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(32) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.12f))
            )
        }
    }
}
