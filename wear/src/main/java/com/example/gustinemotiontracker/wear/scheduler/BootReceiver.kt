package com.example.gustinemotiontracker.wear.scheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.gustinemotiontracker.wear.data.PromptConfigStore

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val config = PromptConfigStore(context).load()
        if (config.enabled) {
            PromptScheduler(context).schedule(config)
        }
    }
}
