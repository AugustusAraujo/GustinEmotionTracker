package com.example.gustinemotiontracker.core.serialization

import com.example.gustinemotiontracker.core.model.MoodEntry
import com.example.gustinemotiontracker.core.model.Quadrant
import com.google.android.gms.wearable.DataMap

/**
 * Converte [MoodEntry] de/para [DataMap] para trafegar pela
 * Wearable Data Layer API (PutDataMapRequest / DataEvent).
 */
object MoodEntrySerializer {

    const val MOOD_ENTRY_PATH = "/mood_entry"

    private const val KEY_TIMESTAMP = "timestamp"
    private const val KEY_QUADRANT = "quadrant"
    private const val KEY_EMOTION = "emotion"
    private const val KEY_HEART_RATE = "heart_rate"

    fun toDataMap(entry: MoodEntry, dataMap: DataMap): DataMap = dataMap.apply {
        putLong(KEY_TIMESTAMP, entry.timestamp)
        putString(KEY_QUADRANT, entry.quadrant.name)
        putString(KEY_EMOTION, entry.emotion)
        if (entry.heartRate != null) {
            putInt(KEY_HEART_RATE, entry.heartRate)
        }
    }

    fun fromDataMap(dataMap: DataMap): MoodEntry = MoodEntry(
        timestamp = dataMap.getLong(KEY_TIMESTAMP),
        quadrant = Quadrant.valueOf(requireNotNull(dataMap.getString(KEY_QUADRANT))),
        emotion = requireNotNull(dataMap.getString(KEY_EMOTION)),
        heartRate = if (dataMap.containsKey(KEY_HEART_RATE)) dataMap.getInt(KEY_HEART_RATE) else null
    )
}
