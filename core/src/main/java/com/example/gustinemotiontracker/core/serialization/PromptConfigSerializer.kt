package com.example.gustinemotiontracker.core.serialization

import com.example.gustinemotiontracker.core.model.PromptConfig
import com.google.android.gms.wearable.DataMap

object PromptConfigSerializer {

    const val PROMPT_CONFIG_PATH = "/prompt_config"

    private const val KEY_ENABLED = "enabled"
    private const val KEY_INTERVAL_HOURS = "interval_hours"
    private const val KEY_WINDOW_START = "window_start_minute"
    private const val KEY_WINDOW_END = "window_end_minute"
    private const val KEY_DAYS_OF_WEEK = "days_of_week"
    private const val KEY_UPDATED_AT = "updated_at_millis"

    fun toDataMap(config: PromptConfig, dataMap: DataMap): DataMap = dataMap.apply {
        putBoolean(KEY_ENABLED, config.enabled)
        putInt(KEY_INTERVAL_HOURS, config.intervalHours)
        putInt(KEY_WINDOW_START, config.windowStartMinute)
        putInt(KEY_WINDOW_END, config.windowEndMinute)
        putString(KEY_DAYS_OF_WEEK, config.daysOfWeek.sorted().joinToString(","))
        putLong(KEY_UPDATED_AT, config.updatedAtMillis)
    }

    fun fromDataMap(dataMap: DataMap): PromptConfig = PromptConfig(
        enabled = dataMap.getBoolean(KEY_ENABLED),
        intervalHours = dataMap.getInt(KEY_INTERVAL_HOURS),
        windowStartMinute = dataMap.getInt(KEY_WINDOW_START),
        windowEndMinute = dataMap.getInt(KEY_WINDOW_END),
        daysOfWeek = requireNotNull(dataMap.getString(KEY_DAYS_OF_WEEK))
            .split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .toSet(),
        updatedAtMillis = dataMap.getLong(KEY_UPDATED_AT)
    )
}
