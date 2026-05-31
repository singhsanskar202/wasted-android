package com.wasted.data.repository

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import com.wasted.data.db.UsageDao
import com.wasted.data.model.DailyUsage
import java.util.Calendar

class UsageRepository(
    private val context: Context,
    private val dao: UsageDao
) {
    private val usageStatsManager by lazy {
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
    }

    suspend fun loadToday(): DailyUsage =
        dao.loadByDate(DailyUsage.todayString()) ?: DailyUsage(date = DailyUsage.todayString())

    suspend fun loadHistory(): List<DailyUsage> =
        dao.loadHistory(excludeDate = DailyUsage.todayString(), limit = 30).reversed()

    suspend fun loadYesterday(): DailyUsage? {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val date = DailyUsage.dateString(cal.time)
        return dao.loadByDate(date)
    }

    suspend fun upsert(usage: DailyUsage) = dao.upsert(usage)

    suspend fun pruneOldData() {
        val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -30) }
        dao.deleteBefore(DailyUsage.dateString(cal.time))
    }

    fun queryUsageStats(): Map<String, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val startMs = cal.timeInMillis
        val endMs = System.currentTimeMillis()
        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startMs, endMs
        ) ?: return emptyMap()
        return stats.filter { it.totalTimeInForeground > 0 }
            .associate { it.packageName to it.totalTimeInForeground }
    }

    // Returns the package name of the app currently in the foreground, or null.
    // Uses a 2-second window looking backwards — the most reliable method without
    // AccessibilityService or shell access.
    fun getForegroundApp(): String? {
        val now = System.currentTimeMillis()
        val events = usageStatsManager.queryEvents(now - 2000, now) ?: return null
        val event = UsageEvents.Event()
        var last: String? = null
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                last = event.packageName
            }
        }
        return last
    }

    suspend fun syncFromUsageStats(trackedPackages: Set<String>) {
        if (trackedPackages.isEmpty()) return
        val raw = queryUsageStats()
        val filtered = raw.filter { it.key in trackedPackages }
        if (filtered.isEmpty()) return

        val today = loadToday()
        val updatedSeconds = filtered.mapValues { (_, ms) -> (ms / 1000).toInt() }

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val previousTotal = today.seconds.values.sum()
        val newTotal = updatedSeconds.values.sum()
        val delta = (newTotal - previousTotal).coerceAtLeast(0)

        val newHourly = today.hourly.toMutableList()
        if (delta > 0) newHourly[currentHour] = newHourly[currentHour] + delta

        upsert(today.copy(seconds = updatedSeconds, hourly = newHourly))
    }
}
