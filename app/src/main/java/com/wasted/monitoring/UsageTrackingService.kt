package com.wasted.monitoring

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.wasted.data.db.AppDatabase
import com.wasted.data.repository.UsageRepository
import com.wasted.prefs.WastedPrefs
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class UsageTrackingService : Service() {
    private val binder = LocalBinder()
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val _totalSeconds = MutableStateFlow(0)
    val totalSeconds: StateFlow<Int> = _totalSeconds

    private val nm by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    inner class LocalBinder : Binder() {
        fun getService(): UsageTrackingService = this@UsageTrackingService
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onCreate() {
        super.onCreate()
        startForeground(
            NotificationHelper.NOTIF_PERSISTENT_ID,
            NotificationHelper.buildPersistentNotification(this, 0)
        )
        scope.launch { pollLoop() }
    }

    private suspend fun pollLoop() {
        val prefs = WastedPrefs(applicationContext)
        val db = AppDatabase.getInstance(applicationContext)
        val repo = UsageRepository(applicationContext, db.usageDao())
        while (true) {
            try {
                val tracked = prefs.trackedPackages.first()
                val names = prefs.displayNames.first()
                if (tracked.isNotEmpty()) {
                    val foreground = repo.getForegroundApp()
                    val activePkg = foreground?.takeIf { it in tracked }

                    if (activePkg != null) {
                        // Live mode: sync + show per-app live timer every second
                        repo.syncFromUsageStats(tracked)
                        val today = repo.loadToday()
                        _totalSeconds.value = today.totalSeconds()
                        val appSeconds = today.seconds[activePkg] ?: 0
                        val appName = names[activePkg] ?: activePkg.substringAfterLast('.')
                        nm.notify(
                            NotificationHelper.NOTIF_PERSISTENT_ID,
                            NotificationHelper.buildLiveNotification(
                                this, appName, appSeconds, _totalSeconds.value
                            )
                        )
                        delay(1_000)
                    } else {
                        // Idle mode: sync every 30s, show total
                        repo.syncFromUsageStats(tracked)
                        _totalSeconds.value = repo.loadToday().totalSeconds()
                        nm.notify(
                            NotificationHelper.NOTIF_PERSISTENT_ID,
                            NotificationHelper.buildPersistentNotification(this, _totalSeconds.value)
                        )
                        delay(30_000)
                    }
                } else {
                    delay(30_000)
                }
            } catch (e: Exception) {
                delay(30_000)
            }
        }
    }

    override fun onDestroy() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        scope.cancel()
        super.onDestroy()
    }
}
