package com.wasted.ui.onboarding

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import com.wasted.monitoring.UsageStatsWorker

data class AppInfo(val packageName: String, val label: String, val icon: Drawable)

class OnboardingViewModel(app: Application) : AndroidViewModel(app) {

    fun installedApps(): List<AppInfo> {
        val pm = getApplication<Application>().packageManager
        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
            .map { AppInfo(it.packageName, pm.getApplicationLabel(it).toString(), pm.getApplicationIcon(it)) }
            .sortedBy { it.label }
    }

    fun scheduleWorker() {
        UsageStatsWorker.schedule(getApplication())
    }
}
