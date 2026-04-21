package com.zenlauncher

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import java.util.concurrent.TimeUnit

class UsageTracker(private val context: Context) {

    private val usageManager: UsageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    fun appUsageToday(packageName: String): Long {
        val end = System.currentTimeMillis()
        val start = end - TimeUnit.DAYS.toMillis(1)
        val stats = usageManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end)
        return stats.firstOrNull { it.packageName == packageName }?.totalTimeInForeground ?: 0L
    }

    fun topUsedAppsToday(limit: Int = 5): List<UsageStats> {
        val end = System.currentTimeMillis()
        val start = end - TimeUnit.DAYS.toMillis(1)
        return usageManager
            .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, start, end)
            .sortedByDescending { it.totalTimeInForeground }
            .take(limit)
    }
}
