package com.wasted.ui.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PatternLockedView(daysLeft: Int) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "$daysLeft",
            fontSize = 48.sp, fontWeight = FontWeight.Bold,
            color = Color.White.copy(alpha = 0.15f)
        )
        Text(
            "more day${if (daysLeft != 1) "s" else ""} until\nyour usage pattern unlocks",
            fontSize = 13.sp, color = Color.White.copy(alpha = 0.25f),
            textAlign = TextAlign.Center, lineHeight = 20.sp
        )
    }
}
