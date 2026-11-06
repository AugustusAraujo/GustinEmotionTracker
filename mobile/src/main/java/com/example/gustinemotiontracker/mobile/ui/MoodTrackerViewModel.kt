package com.example.gustinemotiontracker.mobile.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gustinemotiontracker.core.model.MoodEntry
import com.example.gustinemotiontracker.mobile.MoodTrackerApplication
import com.example.gustinemotiontracker.mobile.data.MoodRepository
import com.example.gustinemotiontracker.mobile.export.ExportPeriod
import com.example.gustinemotiontracker.mobile.export.toMillisRange
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MoodTrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MoodRepository =
        (application as MoodTrackerApplication).repository

    val entries: StateFlow<List<MoodEntry>> = repository.allEntries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    suspend fun entriesIn(period: ExportPeriod): List<MoodEntry> {
        val range = period.toMillisRange()
        return repository.entriesBetween(range.first, range.last)
    }

    suspend fun countIn(period: ExportPeriod): Int {
        val range = period.toMillisRange()
        return repository.countBetween(range.first, range.last)
    }
}
