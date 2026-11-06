package com.example.gustinemotiontracker.mobile.data

import com.example.gustinemotiontracker.core.model.MoodEntry
import kotlinx.coroutines.flow.Flow

class MoodRepository(private val dao: MoodEntryDao) {

    val allEntries: Flow<List<MoodEntry>> = dao.observeAll()

    suspend fun insert(entry: MoodEntry): Long = dao.insert(entry)

    suspend fun entriesBetween(from: Long, to: Long): List<MoodEntry> = dao.getBetween(from, to)

    suspend fun countBetween(from: Long, to: Long): Int = dao.countBetween(from, to)
}
