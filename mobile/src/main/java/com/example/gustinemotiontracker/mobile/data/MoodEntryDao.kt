package com.example.gustinemotiontracker.mobile.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gustinemotiontracker.core.model.MoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodEntryDao {

    @Insert
    suspend fun insert(entry: MoodEntry): Long

    @Query("SELECT * FROM mood_entries ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE timestamp BETWEEN :from AND :to ORDER BY timestamp ASC")
    suspend fun getBetween(from: Long, to: Long): List<MoodEntry>

    @Query("SELECT COUNT(*) FROM mood_entries WHERE timestamp BETWEEN :from AND :to")
    suspend fun countBetween(from: Long, to: Long): Int
}
