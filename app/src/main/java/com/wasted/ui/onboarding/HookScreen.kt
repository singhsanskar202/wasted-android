package com.wasted.ui.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private data class Line(val text: String, val delayMs: Long)

private val LINES = listOf(
    Line("the average person", 0),
    Line("spends 4 hours", 300),
    Line("on their phone.", 600),
    Line("every. single. day.", 1100),
    Line("that's 60 days a year.", 1800),
    Line("gone.", 2600),
)

@Composable
fun HookScreen(onContinue: () -> Unit) {
    val visible = remember { mutableStateListOf(*Array(LINES.size) { false }) }

    LaunchedEffect(Unit) {
        LINES.forEachIndexed { i, line ->
            delay(line.delayMs)
            visible[i] = true
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { onContinue() }
    ) {
        Column(
            Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 32.dp)
        ) {
            LINES.forEachIndexed { i, line ->
                val alpha by animateFloatAsState(
                    targetValue = if (visible.getOrElse(i) { false }) 1f else 0f,
                    animationSpec = tween(600),
                    label = "line$i"
                )
                val isGone = line.text == "gone."
                Text(
                    text = line.text,
                    fontFamily = FontFamily.Serif,
                    fontStyle = if (isGone) FontStyle.Italic else FontStyle.Normal,
                    fontWeight = if (isGone) FontWeight.Bold else FontWeight.Light,
                    fontSize = if (isGone) 42.sp else 28.sp,
                    color = if (isGone) Color.White.copy(alpha = alpha)
                            else Color.White.copy(alpha = 0.85f * alpha),
                    modifier = Modifier.padding(bottom = 14.dp)
                )
            }
        }

        val buttonAlpha by animateFloatAsState(
            targetValue = if (visible.getOrElse(LINES.lastIndex) { false }) 1f else 0f,
            animationSpec = tween(600, delayMillis = 600),
            label = "button"
        )
        Text(
            text = "i want to change this",
            color = Color.White.copy(alpha = 0.5f * buttonAlpha),
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 52.dp)
                .clickable { onContinue() }
        )
    }
}
