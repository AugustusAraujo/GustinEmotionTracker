package com.example.gustinemotiontracker.core.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val timestamp: Long,
    val quadrant: Quadrant,
    val emotion: String,
    val heartRate: Int? = null
)
