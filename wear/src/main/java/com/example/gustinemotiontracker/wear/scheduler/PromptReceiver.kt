package com.example.gustinemotiontracker.wear.scheduler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.gustinemotiontracker.wear.MainActivity
import com.example.gustinemotiontracker.wear.R
import com.example.gustinemotiontracker.wear.data.PromptConfigStore

class PromptReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val config = PromptConfigStore(context).load()
        if (!config.enabled) {
            PromptScheduler(context).cancel()
            return
        }
        Log.i(TAG, "Lembrete disparado; reagendando próximo")
        showNotification(context)
        PromptScheduler(context).schedule(config)
    }

    private fun showNotification(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                "Lembretes de humor",
                NotificationManager.IMPORTANCE_DEFAULT
            )
        )

        val contentIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Como você está?")
            .setContentText("Registre como está se sentindo agora")
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 200, 100, 200))
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val TAG = "PromptReceiver"
        private const val CHANNEL_ID = "mood_prompts"
        private const val NOTIFICATION_ID = 1001
    }
}
