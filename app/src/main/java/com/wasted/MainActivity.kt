package com.wasted

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.wasted.prefs.WastedPrefs
import com.wasted.ui.theme.WastedTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = WastedPrefs(this)
        val isOnboarded = runBlocking { prefs.isOnboarded.first() }

        setContent {
            WastedTheme {
                Surface(color = Color.Black) {
                    // Placeholder — replaced in Tasks 8–10
                    Text(
                        text = if (isOnboarded) "Home Screen (coming soon)" else "Onboarding (coming soon)",
                        color = Color.White
                    )
                }
            }
        }
    }
}
