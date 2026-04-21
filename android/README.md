# Zen Launcher (Android)

Native Kotlin + Jetpack Compose launcher scaffold for a monochrome, text-first "monk mode" workflow.

## What is implemented

- `MainActivity` declared as a launcher (`HOME` + `DEFAULT`) in `AndroidManifest.xml`.
- Compose home screen with:
  - serif typography
  - pure black background / muted text
  - text-only app rows
  - Deep Focus toggle + session countdown
  - 4-digit daily mindfulness code gate before all-app search
  - subtle haptic click on interactions
- `ReelGuardAccessibilityService` to detect Instagram/YouTube/TikTok foreground events.
- `FocusOverlayService` with full-screen interstitial:
  - slow breathing circle
  - IIT future prompt
  - 15 second enforced pause
  - then shows `Open for 5 minutes only` / `Back to Focus` actions
- `UsageTracker` wrapper around `UsageStatsManager` for usage-time queries.

## Permission flows (required)

### 1) Display Over Other Apps

```kotlin
val intent = Intent(
    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
    Uri.parse("package:${packageName}")
)
startActivity(intent)
```

Check with `Settings.canDrawOverlays(context)`.

### 2) Usage Access

```kotlin
startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
```

### 3) Accessibility Service

```kotlin
startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
```

Enable `ReelGuardAccessibilityService` manually in system settings.

## Set as default launcher

`AndroidManifest.xml` includes:

- `android.intent.category.HOME`
- `android.intent.category.DEFAULT`

After install, press Home and choose **Zen Launcher** as default.

## Notes

- Android does not allow a general-purpose app to hard-limit another app to exactly 5 minutes without device-owner or OEM-level controls. The current scaffold displays the UX and launches target app after the gate.
- Add stricter enforcement via device policy APIs if your use case allows managed-device mode.
