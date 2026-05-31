package com.wasted.ui.home

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wasted.monitoring.UsageTrackingService
import com.wasted.ui.home.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow

@Composable
fun HomeScreen(vm: HomeViewModel) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    var liveSeconds by remember { mutableIntStateOf(state.totalSeconds) }
    // Holds the service's totalSeconds flow once bound; null until connected
    var serviceFlow by remember { mutableStateOf<StateFlow<Int>?>(null) }

    // Bind to UsageTrackingService and grab its totalSeconds flow
    DisposableEffect(Unit) {
        val conn = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                serviceFlow = (binder as UsageTrackingService.LocalBinder).getService().totalSeconds
            }
            override fun onServiceDisconnected(name: ComponentName) {
                serviceFlow = null
            }
        }
        val intent = Intent(context, UsageTrackingService::class.java)
        context.startForegroundService(intent)
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE)
        onDispose { context.unbindService(conn) }
    }

    // Collect from the service flow whenever it becomes available
    LaunchedEffect(serviceFlow) {
        serviceFlow?.collect { liveSeconds = it }
    }

    // Refresh ViewModel every 30s so insight cards stay fresh
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            vm.refresh()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Quote
        Text(
            state.quote,
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Light,
            fontSize = 15.sp,
            color = Color.White.copy(alpha = 0.35f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier
                .padding(horizontal = 40.dp)
                .padding(top = 52.dp, bottom = 32.dp)
        )

        Spacer(Modifier.height(24.dp))
        ReceiptCard(
            totalSeconds = liveSeconds,
            appSeconds = state.today.seconds,
            displayNames = state.displayNames
        )
        Spacer(Modifier.height(40.dp))

        // Heatmap or locked state
        if (state.daysLeft == 0 && state.today.hourly.any { it > 0 }) {
            HeatmapView(state.today)
        } else {
            PatternLockedView(state.daysLeft)
        }
        Spacer(Modifier.height(40.dp))

        // Insight cards
        state.insightResult?.let { result ->
            Column(Modifier.padding(horizontal = 20.dp)) {
                DangerZonesCard(result)
                result.weekly?.let { weekly ->
                    Spacer(Modifier.height(16.dp))
                    WeeklyCard(weekly)
                }
            }
        }

        Spacer(Modifier.height(40.dp))
        HorizontalDivider(
            color = Color.White.copy(alpha = 0.07f),
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(Modifier.height(52.dp))
    }
}
