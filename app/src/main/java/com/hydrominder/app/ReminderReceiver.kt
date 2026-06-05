package com.hydrominder.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ReminderScheduler.ACTION_REMIND) return

        val serviceIntent = Intent(context, ReminderForegroundService::class.java)
        ContextCompat.startForegroundService(context, serviceIntent)
        ReminderScheduler.scheduleNext(context)
    }
}
