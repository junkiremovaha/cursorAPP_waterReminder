package com.hydrominder.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.core.app.NotificationCompat
import java.util.Locale

class ReminderForegroundService : Service(), TextToSpeech.OnInitListener {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var textToSpeech: TextToSpeech? = null
    private var previousMusicVolume: Int? = null
    private var isFinishing = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
        textToSpeech = TextToSpeech(this, this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onInit(status: Int) {
        if (status != TextToSpeech.SUCCESS) {
            finishReminder()
            return
        }

        val tts = textToSpeech ?: run {
            finishReminder()
            return
        }

        tts.language = Locale.US
        tts.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build(),
        )
        tts.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) = Unit

                override fun onDone(utteranceId: String?) {
                    finishReminder()
                }

                override fun onError(utteranceId: String?) {
                    finishReminder()
                }
            },
        )

        raiseMediaVolumeToMax()
        val result = tts.speak(REMINDER_TEXT, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)
        if (result == TextToSpeech.ERROR) {
            finishReminder()
        }
    }

    override fun onDestroy() {
        restoreMediaVolume()
        textToSpeech?.shutdown()
        textToSpeech = null
        super.onDestroy()
    }

    private fun raiseMediaVolumeToMax() {
        val audioManager = getSystemService(AudioManager::class.java)
        previousMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
            0,
        )
    }

    private fun restoreMediaVolume() {
        val originalVolume = previousMusicVolume ?: return
        previousMusicVolume = null

        val audioManager = getSystemService(AudioManager::class.java)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0)
    }

    private fun finishReminder() {
        mainHandler.post {
            if (isFinishing) return@post

            isFinishing = true
            restoreMediaVolume()
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            textToSpeech = null
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun buildNotification() =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_water_drop)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Playing water reminder")
            .setContentIntent(mainActivityIntent())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .build()

    private fun mainActivityIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "HydroMinder reminders",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Shows while HydroMinder speaks a water reminder."
        }

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private companion object {
        const val CHANNEL_ID = "hydrominder_reminder_channel"
        const val NOTIFICATION_ID = 1001
        const val REMINDER_TEXT = "Drink water please"
        const val UTTERANCE_ID = "hydrominder_drink_water"
    }
}
