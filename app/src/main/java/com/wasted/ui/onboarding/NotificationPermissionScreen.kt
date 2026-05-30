package com.wasted.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotificationPermissionScreen(onContinue: () -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { onContinue() }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(32.dp)
    ) {
        Column(Modifier.align(Alignment.CenterStart)) {
            Text(
                "nudges\nwhen it counts.",
                fontSize = 32.sp, fontWeight = FontWeight.Bold,
                color = Color.White, lineHeight = 40.sp
            )
            Spacer(Modifier.height(20.dp))
            Text(
                "Wasted sends one notification per hour milestone. Nothing else.",
                fontSize = 16.sp, color = Color.White.copy(alpha = 0.5f), lineHeight = 24.sp
            )
        }
        Column(
            Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        onContinue()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                )
            ) {
                Text("Allow Notifications", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            TextButton(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
                Text("Skip", color = Color.White.copy(alpha = 0.3f), fontSize = 14.sp)
            }
        }
    }
}
