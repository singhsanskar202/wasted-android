package com.wasted.ui.home

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
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
import com.wasted.ui.theme.WastedOrange
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(vm: HomeViewModel) {
    val state by vm.state.collectAsState()
    val context = LocalContext.current
    var liveSeconds by remember { mutableIntStateOf(state.totalSeconds) }

    // Bind to UsageTrackingService for live counter
    DisposableEffect(Unit) {
        val conn = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, binder: IBinder) {
                // observe service total — propagate to liveSeconds
            }
            override fun onServiceDisconnected(name: ComponentName) {}
        }
        val intent = Intent(context, UsageTrackingService::class.java)
        context.startForegroundService(intent)
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE)
        onDispose { context.unbindService(conn) }
    }

    // Refresh ViewModel every 30s
    LaunchedEffect(Unit) {
        while (true) {
            delay(30_000)
            vm.refresh()
        }
    }

    // Sync live seconds from state
    LaunchedEffect(state.totalSeconds) { liveSeconds = state.totalSeconds }

    val animatedSeconds by animateIntAsState(
        targetValue = liveSeconds,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow),
        label = "counter"
    )
    val h = animatedSeconds / 3600
    val m = (animatedSeconds % 3600) / 60
    val timeString = when {
        h > 0 -> "${h}h ${m}m"
        m > 0 -> "${m}m"
        else  -> "0m"
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
                .padding(top = 72.dp, bottom = 60.dp)
        )

        // Big counter
        Text(
            "you wasted",
            fontSize = 14.sp, fontWeight = FontWeight.Light,
            letterSpacing = 2.sp, color = Color.White.copy(alpha = 0.35f)
        )
        Text(
            timeString,
            fontSize = 72.sp, fontWeight = FontWeight.Bold, color = Color.White
        )
        Text(
            "on your phone today",
            fontSize = 14.sp, fontWeight = FontWeight.Light,
            letterSpacing = 2.sp, color = Color.White.copy(alpha = 0.35f)
        )
        Spacer(Modifier.height(32.dp))

        // Equivalent task
        state.equivalent?.let { eq ->
            Text(
                "that's ${eq.description} ${eq.emoji}",
                fontSize = 15.sp,
                color = WastedOrange.copy(alpha = 0.75f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 40.dp)
            )
            Spacer(Modifier.height(60.dp))
        } ?: Spacer(Modifier.height(60.dp))

        HorizontalDivider(
            color = Color.White.copy(alpha = 0.07f),
            modifier = Modifier.padding(horizontal = 32.dp)
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
