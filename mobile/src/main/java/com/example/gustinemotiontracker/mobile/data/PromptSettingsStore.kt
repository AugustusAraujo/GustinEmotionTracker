package com.example.gustinemotiontracker.mobile.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.gustinemotiontracker.core.model.PromptConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.promptDataStore: DataStore<Preferences> by preferencesDataStore(name = "prompt_settings")

class PromptSettingsStore(private val context: Context) {

    private object Keys {
        val ENABLED = booleanPreferencesKey("enabled")
        val INTERVAL = intPreferencesKey("interval_hours")
        val WINDOW_START = intPreferencesKey("window_start_minute")
        val WINDOW_END = intPreferencesKey("window_end_minute")
        val DAYS = stringPreferencesKey("days_of_week")
        val UPDATED_AT = longPreferencesKey("updated_at_millis")
    }

    val config: Flow<PromptConfig> = context.promptDataStore.data.map { prefs ->
        PromptConfig(
            enabled = prefs[Keys.ENABLED] ?: false,
            intervalHours = prefs[Keys.INTERVAL] ?: 2,
            windowStartMinute = prefs[Keys.WINDOW_START] ?: 8 * 60,
            windowEndMinute = prefs[Keys.WINDOW_END] ?: 22 * 60,
            daysOfWeek = (prefs[Keys.DAYS] ?: "1,2,3,4,5,6,7")
                .split(",")
                .mapNotNull { it.trim().toIntOrNull() }
                .toSet()
                .ifEmpty { setOf(1, 2, 3, 4, 5, 6, 7) },
            updatedAtMillis = prefs[Keys.UPDATED_AT] ?: 0L
        )
    }

    suspend fun save(config: PromptConfig) {
        context.promptDataStore.edit { prefs ->
            prefs[Keys.ENABLED] = config.enabled
            prefs[Keys.INTERVAL] = config.intervalHours
            prefs[Keys.WINDOW_START] = config.windowStartMinute
            prefs[Keys.WINDOW_END] = config.windowEndMinute
            prefs[Keys.DAYS] = config.daysOfWeek.sorted().joinToString(",")
            prefs[Keys.UPDATED_AT] = config.updatedAtMillis
        }
    }
}
