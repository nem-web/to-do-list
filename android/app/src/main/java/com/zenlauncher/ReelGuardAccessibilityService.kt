package com.zenlauncher

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.view.accessibility.AccessibilityEvent

class ReelGuardAccessibilityService : AccessibilityService() {

    private val reelApps = setOf(
        "com.instagram.android",
        "com.google.android.youtube",
        "com.zhiliaoapp.musically"
    )

    override fun onServiceConnected() {
        serviceInfo = serviceInfo.apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 80
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val packageName = event?.packageName?.toString() ?: return
        if (packageName in reelApps) {
            performGlobalAction(GLOBAL_ACTION_HOME)
            val intent = Intent(this, FocusOverlayService::class.java)
                .putExtra(FocusOverlayService.EXTRA_TARGET_PACKAGE, packageName)
            startService(intent)
        }
    }

    override fun onInterrupt() = Unit
}
