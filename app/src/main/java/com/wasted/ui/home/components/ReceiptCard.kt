package com.wasted.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Red = Color(0xFFFF3B30)
private val Orange = Color(0xFFFF6B35)

@Composable
fun ReceiptCard(
    totalSeconds: Int,
    appSeconds: Map<String, Int>,
    displayNames: Map<String, String>,
    modifier: Modifier = Modifier
) {
    val topApps = appSeconds.entries.sortedByDescending { it.value }.take(5)
    val pct = (totalSeconds / 86400f * 100).toInt().coerceAtMost(100)

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp)) {

        // Hero row: big seconds + ring
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "%,d".format(totalSeconds),
                    fontSize = 58.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-2).sp,
                    lineHeight = 58.sp
                )
                Text(
                    text = "seconds wasted today",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.25f),
                    letterSpacing = 0.3.sp
                )
            }
            Spacer(Modifier.width(16.dp))
            // Percentage ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(68.dp)
                    .border(2.dp, Red.copy(alpha = 0.35f), RoundedCornerShape(50))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$pct%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Red,
                        lineHeight = 20.sp
                    )
                    Text(
                        text = "of day",
                        fontSize = 8.sp,
                        color = Color.White.copy(alpha = 0.25f)
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Divider()
        Spacer(Modifier.height(16.dp))

        // App rows
        if (topApps.isEmpty()) {
            Text(
                text = "no tracked apps used today",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.25f)
            )
        } else {
            topApps.forEach { (pkg, seconds) ->
                val name = displayNames[pkg] ?: pkg.substringAfterLast('.')
                val fraction = if (totalSeconds > 0) seconds.toFloat() / totalSeconds else 0f
                AppRow(name = name, seconds = seconds, fraction = fraction)
                Spacer(Modifier.height(12.dp))
            }
        }

        Spacer(Modifier.height(4.dp))
        Divider()
        Spacer(Modifier.height(14.dp))

        // Closing blockquote
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp))
                .background(Red.copy(alpha = 0.05f))
        ) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(36.dp)
                    .background(Red)
            )
            Text(
                text = "these seconds are non-refundable.",
                fontSize = 13.sp,
                fontStyle = FontStyle.Italic,
                color = Red.copy(alpha = 0.7f),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun AppRow(name: String, seconds: Int, fraction: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = name,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier.width(90.dp),
            maxLines = 1
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color.White.copy(alpha = 0.07f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction.coerceIn(0f, 1f))
                    .background(
                        Brush.horizontalGradient(listOf(Red, Orange))
                    )
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = "%,ds".format(seconds),
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.width(56.dp)
        )
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.White.copy(alpha = 0.06f))
    )
}
