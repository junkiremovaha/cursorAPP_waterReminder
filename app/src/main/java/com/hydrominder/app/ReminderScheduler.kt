package com.hydrominder.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build

object ReminderScheduler {
    const val ACTION_REMIND = "com.hydrominder.app.action.REMIND"

    fun ensureScheduled(context: Context) {
        val now = System.currentTimeMillis()
        val nextReminderAt = ReminderPreferences.getNextReminderAtMillis(context)

        if (nextReminderAt <= now) {
            scheduleNext(context)
        } else {
            scheduleAt(context, nextReminderAt)
        }
    }

    fun scheduleNext(
        context: Context,
        intervalMillis: Long = ReminderPreferences.getIntervalMillis(context),
    ): Long {
        val triggerAtMillis = System.currentTimeMillis() + intervalMillis.coerceAtLeast(60_000L)
        ReminderPreferences.setNextReminderAtMillis(context, triggerAtMillis)
        scheduleAt(context, triggerAtMillis)
        return triggerAtMillis
    }

    fun canScheduleExactAlarms(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true

        val alarmManager = context.getSystemService(AlarmManager::class.java)
        return alarmManager.canScheduleExactAlarms()
    }

    private fun scheduleAt(context: Context, triggerAtMillis: Long) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val pendingIntent = reminderPendingIntent(context)

        if (canScheduleExactAlarms(context)) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent,
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent,
            )
        }
    }

    private fun reminderPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_REMIND
        }

        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
