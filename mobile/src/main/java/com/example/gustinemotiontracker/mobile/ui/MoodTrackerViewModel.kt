package com.example.gustinemotiontracker.mobile.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gustinemotiontracker.core.model.MoodEntry
import com.example.gustinemotiontracker.core.model.PromptConfig
import com.example.gustinemotiontracker.core.serialization.PromptConfigSerializer
import com.example.gustinemotiontracker.mobile.MoodTrackerApplication
import com.example.gustinemotiontracker.mobile.data.MoodRepository
import com.example.gustinemotiontracker.mobile.data.PromptSettingsStore
import com.example.gustinemotiontracker.mobile.export.ExportPeriod
import com.example.gustinemotiontracker.mobile.export.toMillisRange
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MoodTrackerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MoodRepository =
        (application as MoodTrackerApplication).repository

    private val promptSettingsStore = PromptSettingsStore(application)

    val entries: StateFlow<List<MoodEntry>> = repository.allEntries.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val promptConfig: StateFlow<PromptConfig> = promptSettingsStore.config.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PromptConfig()
    )

    private val _promptSyncState = MutableStateFlow<Boolean?>(null)
    val promptSyncState: StateFlow<Boolean?> = _promptSyncState.asStateFlow()

    fun updatePromptConfig(transform: (PromptConfig) -> PromptConfig) {
        viewModelScope.launch {
            val updated = transform(promptConfig.value)
                .copy(updatedAtMillis = System.currentTimeMillis())
            promptSettingsStore.save(updated)
            pushPromptConfig(updated)
        }
    }

    private fun pushPromptConfig(config: PromptConfig) {
        _promptSyncState.value = null
        val request = PutDataMapRequest.create(PromptConfigSerializer.PROMPT_CONFIG_PATH)
        PromptConfigSerializer.toDataMap(config, request.dataMap)
        request.setUrgent()
        Wearable.getDataClient(getApplication()).putDataItem(request.asPutDataRequest())
            .addOnSuccessListener {
                Log.i(TAG, "PromptConfig enviada: enabled=${config.enabled}")
                _promptSyncState.value = true
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Falha ao enviar PromptConfig", e)
                _promptSyncState.value = false
            }
    }

    suspend fun entriesIn(period: ExportPeriod): List<MoodEntry> {
        val range = period.toMillisRange()
        return repository.entriesBetween(range.first, range.last)
    }

    suspend fun countIn(period: ExportPeriod): Int {
        val range = period.toMillisRange()
        return repository.countBetween(range.first, range.last)
    }

    fun deleteEntry(entry: MoodEntry) {
        viewModelScope.launch { repository.deleteById(entry.id) }
    }

    fun undoDelete(entry: MoodEntry) {
        viewModelScope.launch { repository.insert(entry) }
    }

    fun deleteAll() {
        viewModelScope.launch { repository.deleteAll() }
    }

    private companion object {
        const val TAG = "MoodTrackerViewModel"
    }
}
