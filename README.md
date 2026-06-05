HydroMinder
============

HydroMinder is a native Android app built with Kotlin and Jetpack Compose. It
shows a live countdown to the next water reminder, lets you adjust the reminder
interval from 1 minute to 4 hours, and uses Android exact alarms to keep
reminders on schedule while the app is closed.

When a reminder fires, HydroMinder starts a foreground service, raises the media
volume to maximum, speaks "Drink water please" with Android Text-to-Speech, and
then restores the previous media volume.

Key implementation points
-------------------------

- UI: Jetpack Compose dashboard in `MainActivity.kt`
- Scheduling: `AlarmManager.setExactAndAllowWhileIdle` in `ReminderScheduler.kt`
- Playback: foreground service and Text-to-Speech in
  `ReminderForegroundService.kt`
- Persistence: `SharedPreferences` in `ReminderPreferences.kt`
- Resilience: boot receiver reschedules reminders after device restart

Build
-----

```bash
./gradlew assembleDebug
```

The app targets Android SDK 36 and supports Android 8.0+.

