package com.wasted.ui.onboarding

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wasted.ui.theme.WastedOrange

@Composable
fun PermissionScreen(onContinue: () -> Unit) {
    val context = LocalContext.current
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(32.dp)
    ) {
        Column(Modifier.align(Alignment.CenterStart)) {
            Text(
                "we need to\nsee your screen time.",
                fontSize = 32.sp, fontWeight = FontWeight.Bold,
                color = Color.White, lineHeight = 40.sp
            )
            Spacer(Modifier.height(20.dp))
            Text(
                "Grant Usage Access so Wasted can track how long you spend in each app.",
                fontSize = 16.sp, color = Color.White.copy(alpha = 0.5f), lineHeight = 24.sp
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Settings → Wasted → Permit Usage Access",
                fontSize = 13.sp, color = WastedOrange.copy(alpha = 0.75f)
            )
        }
        Button(
            onClick = {
                context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                onContinue()
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Text("Open Settings", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}
