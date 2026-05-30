package com.wasted

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wasted.prefs.WastedPrefs
import com.wasted.ui.home.HomeScreen
import com.wasted.ui.home.HomeViewModel
import com.wasted.ui.onboarding.OnboardingScreen
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
                if (isOnboarded) {
                    val vm: HomeViewModel = viewModel()
                    HomeScreen(vm)
                } else {
                    OnboardingScreen(prefs = prefs, onComplete = { recreate() })
                }
            }
        }
    }
}
