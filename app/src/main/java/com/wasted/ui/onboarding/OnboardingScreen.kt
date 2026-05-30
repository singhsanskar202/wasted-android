package com.wasted.ui.onboarding

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wasted.prefs.WastedPrefs
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    prefs: WastedPrefs,
    onComplete: () -> Unit,
    vm: OnboardingViewModel = viewModel()
) {
    var step by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    Crossfade(targetState = step, label = "onboarding") { current ->
        when (current) {
            0 -> HookScreen { step = 1 }
            1 -> PermissionScreen { step = 2 }
            2 -> NotificationPermissionScreen { step = 3 }
            3 -> AppPickerScreen(vm) { packages, names ->
                scope.launch {
                    prefs.setTrackedPackages(packages, names)
                    vm.scheduleWorker()
                    step = 4
                }
            }
            else -> DoneScreen {
                scope.launch {
                    prefs.setOnboarded()
                    onComplete()
                }
            }
        }
    }
}
