package com.example.gustinemotiontracker.wear.data

import android.content.Context
import com.example.gustinemotiontracker.core.model.PromptConfig

class PromptConfigStore(context: Context) {

    private val prefs =
        context.getSharedPreferences("prompt_config", Context.MODE_PRIVATE)

    fun save(config: PromptConfig) {
        prefs.edit()
            .putBoolean(KEY_ENABLED, config.enabled)
            .putInt(KEY_INTERVAL, config.intervalHours)
            .putInt(KEY_WINDOW_START, config.windowStartMinute)
            .putInt(KEY_WINDOW_END, config.windowEndMinute)
            .putString(KEY_DAYS, config.daysOfWeek.sorted().joinToString(","))
            .putLong(KEY_UPDATED_AT, config.updatedAtMillis)
            .apply()
    }

    fun load(): PromptConfig = PromptConfig(
        enabled = prefs.getBoolean(KEY_ENABLED, false),
        intervalHours = prefs.getInt(KEY_INTERVAL, 2),
        windowStartMinute = prefs.getInt(KEY_WINDOW_START, 8 * 60),
        windowEndMinute = prefs.getInt(KEY_WINDOW_END, 22 * 60),
        daysOfWeek = (prefs.getString(KEY_DAYS, "1,2,3,4,5,6,7") ?: "1,2,3,4,5,6,7")
            .split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet()
            .ifEmpty { setOf(1, 2, 3, 4, 5, 6, 7) },
        updatedAtMillis = prefs.getLong(KEY_UPDATED_AT, 0L)
    )

    private companion object {
        const val KEY_ENABLED = "enabled"
        const val KEY_INTERVAL = "interval_hours"
        const val KEY_WINDOW_START = "window_start_minute"
        const val KEY_WINDOW_END = "window_end_minute"
        const val KEY_DAYS = "days_of_week"
        const val KEY_UPDATED_AT = "updated_at_millis"
    }
}
