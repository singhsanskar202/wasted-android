package com.wasted

import android.app.Application
import androidx.work.Configuration
import com.wasted.monitoring.NotificationHelper
import com.wasted.monitoring.UsageStatsWorker

class WastedApp : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
        UsageStatsWorker.schedule(this)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().build()
}
