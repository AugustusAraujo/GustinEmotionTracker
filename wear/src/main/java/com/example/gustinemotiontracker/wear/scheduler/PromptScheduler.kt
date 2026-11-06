package com.example.gustinemotiontracker.wear.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.gustinemotiontracker.core.model.PromptConfig
import java.time.Instant
import java.time.ZoneId

class PromptScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedule(config: PromptConfig) {
        cancel()
        if (!config.enabled) return
        val next = nextTriggerMillis(config, System.currentTimeMillis()) ?: return
        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next, pendingIntent())
    }

    fun cancel() {
        alarmManager.cancel(pendingIntent())
    }

    private fun pendingIntent(): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(context, PromptReceiver::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private companion object {
        const val REQUEST_CODE = 1001
    }
}

internal fun nextTriggerMillis(config: PromptConfig, nowMillis: Long): Long? {
    val zone = ZoneId.systemDefault()
    val nowDate = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
    val intervalMillis = config.intervalHours * 3_600_000L

    for (dayOffset in 0..15) {
        val date = nowDate.plusDays(dayOffset.toLong())
        if (date.dayOfWeek.value !in config.daysOfWeek) continue

        val windowStart = atMinuteOfDay(date, config.windowStartMinute, zone)
        val windowEnd = atMinuteOfDay(date, config.windowEndMinute, zone)

        var trigger = windowStart
        var iteration = 0
        while (trigger <= windowEnd && iteration < 48) {
            if (trigger > nowMillis) return trigger
            trigger += intervalMillis
            iteration++
        }
    }
    return null
}

private fun atMinuteOfDay(date: java.time.LocalDate, minuteOfDay: Int, zone: ZoneId): Long =
    date.atTime(minuteOfDay / 60, minuteOfDay % 60)
        .atZone(zone)
        .toInstant()
        .toEpochMilli()
