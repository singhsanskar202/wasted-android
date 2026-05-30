package com.wasted.monitoring

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.wasted.data.db.AppDatabase
import com.wasted.data.repository.UsageRepository
import com.wasted.prefs.WastedPrefs
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class UsageStatsWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return try {
            val prefs = WastedPrefs(applicationContext)
            val trackedPackages = prefs.trackedPackages.first()
            if (trackedPackages.isEmpty()) return Result.success()

            val db = AppDatabase.getInstance(applicationContext)
            val repo = UsageRepository(applicationContext, db.usageDao())

            repo.syncFromUsageStats(trackedPackages)
            repo.pruneOldData()

            val today = repo.loadToday()
            val totalSeconds = today.totalSeconds()

            updatePersistentNotification(totalSeconds)
            checkMilestones(totalSeconds, prefs)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun updatePersistentNotification(totalSeconds: Int) {
        val nm = NotificationManagerCompat.from(applicationContext)
        if (nm.areNotificationsEnabled()) {
            nm.notify(
                NotificationHelper.NOTIF_PERSISTENT_ID,
                NotificationHelper.buildPersistentNotification(applicationContext, totalSeconds)
            )
        }
    }

    private suspend fun checkMilestones(totalSeconds: Int, prefs: WastedPrefs) {
        val hours = totalSeconds / 3600
        if (hours <= 0) return
        val lastNotified = prefs.lastMilestoneHour.first()
        if (hours > lastNotified) {
            NotificationHelper.showMilestoneNotification(applicationContext, hours)
            prefs.setLastMilestoneHour(hours)
        }
    }

    companion object {
        const val WORK_NAME = "usage_stats_poll"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<UsageStatsWorker>(15, TimeUnit.MINUTES).build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, request
            )
        }
    }
}
