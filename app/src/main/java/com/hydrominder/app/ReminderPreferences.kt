package com.hydrominder.app

import android.content.Context

object ReminderPreferences {
    private const val PREFERENCES_NAME = "hydrominder_preferences"
    private const val KEY_INTERVAL_MILLIS = "interval_millis"
    private const val KEY_NEXT_REMINDER_AT_MILLIS = "next_reminder_at_millis"

    const val DEFAULT_INTERVAL_MILLIS: Long = 60 * 60 * 1000L

    fun getIntervalMillis(context: Context): Long {
        return preferences(context).getLong(KEY_INTERVAL_MILLIS, DEFAULT_INTERVAL_MILLIS)
    }

    fun setIntervalMillis(context: Context, intervalMillis: Long) {
        preferences(context)
            .edit()
            .putLong(KEY_INTERVAL_MILLIS, intervalMillis.coerceAtLeast(60_000L))
            .apply()
    }

    fun getNextReminderAtMillis(context: Context): Long {
        return preferences(context).getLong(KEY_NEXT_REMINDER_AT_MILLIS, 0L)
    }

    fun setNextReminderAtMillis(context: Context, triggerAtMillis: Long) {
        preferences(context)
            .edit()
            .putLong(KEY_NEXT_REMINDER_AT_MILLIS, triggerAtMillis)
            .apply()
    }

    private fun preferences(context: Context) =
        context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
}
