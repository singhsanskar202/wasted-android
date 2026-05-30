package com.wasted.monitoring

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.wasted.MainActivity

object NotificationHelper {
    const val CHANNEL_PERSISTENT = "wasted_persistent"
    const val CHANNEL_MILESTONE  = "wasted_milestones"
    const val NOTIF_PERSISTENT_ID = 1
    private const val NOTIF_MILESTONE_ID = 2

    fun createChannels(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.createNotificationChannel(NotificationChannel(
            CHANNEL_PERSISTENT, "Usage Tracking",
            NotificationManager.IMPORTANCE_LOW
        ).apply { setShowBadge(false) })
        nm.createNotificationChannel(NotificationChannel(
            CHANNEL_MILESTONE, "Hourly Milestones",
            NotificationManager.IMPORTANCE_DEFAULT
        ))
    }

    fun buildPersistentNotification(context: Context, totalSeconds: Int) =
        NotificationCompat.Builder(context, CHANNEL_PERSISTENT)
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .setContentTitle(formatTime(totalSeconds) + " today")
            .setContentText("Tap to see your usage")
            .setOngoing(true)
            .setSilent(true)
            .setContentIntent(
                PendingIntent.getActivity(
                    context, 0,
                    Intent(context, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .build()

    fun showMilestoneNotification(context: Context, hours: Int) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notif = NotificationCompat.Builder(context, CHANNEL_MILESTONE)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("$hours hour${if (hours > 1) "s" else ""} on your phone")
            .setContentText("That's ${hours * 60} minutes gone today.")
            .setAutoCancel(true)
            .build()
        nm.notify(NOTIF_MILESTONE_ID, notif)
    }

    fun formatTime(totalSeconds: Int): String {
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        return if (h > 0) "${h}h ${m}m" else "${m}m"
    }
}
