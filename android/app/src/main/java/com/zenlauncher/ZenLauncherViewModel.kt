package com.zenlauncher

import androidx.lifecycle.ViewModel
import java.security.MessageDigest
import java.time.LocalDate
import java.util.Locale

data class AppEntry(val label: String, val packageName: String)

class ZenLauncherViewModel : ViewModel() {
    val essentialApps = listOf(
        AppEntry("Phone", "com.google.android.dialer"),
        AppEntry("Messages", "com.google.android.apps.messaging"),
        AppEntry("Notes", "com.google.android.keep"),
        AppEntry("Study Tool", "com.google.android.apps.docs")
    )

    private val allApps = listOf(
        AppEntry("Instagram", "com.instagram.android"),
        AppEntry("YouTube", "com.google.android.youtube"),
        AppEntry("TikTok", "com.zhiliaoapp.musically"),
        AppEntry("Chrome", "com.android.chrome"),
        AppEntry("Calendar", "com.google.android.calendar")
    ) + essentialApps

    private val blockedApps = setOf(
        "com.instagram.android",
        "com.google.android.youtube",
        "com.zhiliaoapp.musically"
    )

    var deepFocusEnabled: Boolean = false
        private set

    private var focusSeconds = 25 * 60

    fun toggleFocus(enabled: Boolean) {
        deepFocusEnabled = enabled
        if (!enabled) focusSeconds = 25 * 60
    }

    fun tickFocusTimer() {
        if (focusSeconds > 0) focusSeconds--
    }

    fun formattedFocusTime(): String {
        val minutes = focusSeconds / 60
        val seconds = focusSeconds % 60
        return String.format(Locale.US, "%02d:%02d", minutes, seconds)
    }

    fun validateMindfulnessCode(input: String): Boolean = input == dailyMindfulnessCode()

    fun filteredApps(query: String): List<AppEntry> {
        if (query.isBlank()) return allApps.sortedBy { it.label }
        return allApps.filter { it.label.contains(query, ignoreCase = true) }.sortedBy { it.label }
    }

    fun isBlockedApp(packageName: String): Boolean = deepFocusEnabled && packageName in blockedApps

    private fun dailyMindfulnessCode(): String {
        val dayKey = LocalDate.now().toString()
        val hash = MessageDigest.getInstance("SHA-256").digest(dayKey.toByteArray())
        val number = ((hash[0].toInt() and 0xFF) shl 8) + (hash[1].toInt() and 0xFF)
        return String.format(Locale.US, "%04d", number % 10000)
    }
}
