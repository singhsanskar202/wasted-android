package com.wasted.ui.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DoneScreen(onDone: () -> Unit) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val alpha1 by animateFloatAsState(if (visible) 1f else 0f, tween(600), label = "h")
    val alpha2 by animateFloatAsState(if (visible) 1f else 0f, tween(600, delayMillis = 400), label = "sub")
    val alpha3 by animateFloatAsState(if (visible) 1f else 0f, tween(500, delayMillis = 900), label = "btn")

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(32.dp)
    ) {
        Column(Modifier.align(Alignment.CenterStart)) {
            Text(
                "no hiding\nnow.",
                fontSize = 42.sp, fontWeight = FontWeight.Bold,
                color = Color.White, lineHeight = 50.sp,
                modifier = Modifier.graphicsLayer { alpha = alpha1 }
            )
            Spacer(Modifier.height(20.dp))
            Text(
                "every minute tracked.\nshown in your notification.\nnudged when it matters.",
                fontSize = 17.sp, color = Color.White.copy(alpha = 0.5f), lineHeight = 26.sp,
                modifier = Modifier.graphicsLayer { alpha = alpha2 }
            )
        }
        Button(
            onClick = onDone,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(56.dp)
                .graphicsLayer { alpha = alpha3 },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Text("let's go", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}
